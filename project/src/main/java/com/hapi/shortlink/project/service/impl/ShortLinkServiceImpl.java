package com.hapi.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.project.common.convention.exception.ServiceException;
import com.hapi.shortlink.project.dao.entity.BaseDO;
import com.hapi.shortlink.project.dao.entity.ShortLinkDO;
import com.hapi.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.hapi.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.hapi.shortlink.project.dao.mapper.ShortLinkMapper;
import com.hapi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCountRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.hapi.shortlink.project.service.ShortLinkService;
import com.hapi.shortlink.project.util.HashUtil;
import com.hapi.shortlink.project.util.ShortLinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hapi.shortlink.project.common.constant.RedisKeyConstant.*;
import static com.hapi.shortlink.project.common.constant.ShortLInkConstant.DEFAULT_NULL_CACHE_EXPIRE_TIME;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private static final int MAX_GENERATE_SUFFIX_TRY_TIME = 10;
    private final RBloomFilter<String> shortLinkBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUrlSuffix = getShortUrlSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);

        String fullShortUrl = requestParam.getDomain() + '/' + shortUrlSuffix;

        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setShortUri(shortUrlSuffix);
        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl).gid(requestParam.getGid()).build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(String.format("短链接：%s 重复", shortLinkDO.getFullShortUrl()));
        }
        shortLinkBloomFilter.add(fullShortUrl);
        stringRedisTemplate.opsForValue()
                .set(
                        String.format(SHORT_LINK_GOTO_CACHE_KEY, shortLinkDO.getFullShortUrl()),
                        shortLinkDO.getOriginalUrl(),
                        ShortLinkUtil.getExpireTime(shortLinkDO),
                        TimeUnit.MILLISECONDS
                );

        return ShortLinkCreateRespDTO.builder()
                .gid(requestParam.getGid())
                .originalUrl(requestParam.getOriginalUrl())
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> getShortLinkByPage(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getEnableFlag, 1)
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .orderByDesc(BaseDO::getCreateTime);
        IPage<ShortLinkDO> result = baseMapper.selectPage(requestParam, wrapper);
        return result.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
    }

    @Override
    public ShortLinkCountRespDTO getShortLinkCount(List<String> gidlist, String username) {
        QueryWrapper<ShortLinkDO> wrapper = Wrappers.query(new ShortLinkDO())
                .select( "gid, COUNT(*) AS count")
                .in("gid", gidlist)
                .eq("username", username)
                .groupBy("gid");
        List<Map<String, Object>> resultList = baseMapper.selectMaps(wrapper);
        return new ShortLinkCountRespDTO(resultList.stream().
                collect(Collectors.toMap(
                        map -> (String) map.get("gid"), // 键：gid
                        map -> (Long) map.get("count")  // 值：count
                        )
                )
        );
    }

    @Override
    public void redirectShortLink(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String fullShortUrl = request.getServerName() + '/' + shortUri;
        String originalUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_GOTO_CACHE_KEY, fullShortUrl));
        //先查缓存，若有则直接跳转
        if(StrUtil.isNotBlank(originalUrl)) {
            ((HttpServletResponse)response).sendRedirect(originalUrl);
            return;
        }

        //缓存中不存在，查看布隆过滤器是否存在，若无则直接返回
        if(!shortLinkBloomFilter.contains(fullShortUrl)) {
            ((HttpServletResponse)response).sendRedirect("/page/notfound");
            return;
        }

        //布隆过滤器中存在，为防止误判，先查空对象缓存，若被缓存说明该链接确实不存在
        String isEmpty = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_GOTO_NULL_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(isEmpty)) {
            ((HttpServletResponse)response).sendRedirect("/page/notfound");
            return;
        }

        //缓存中没有，布隆过滤器中存在，查数据库前先通过分布式锁防止所有请求都去访问数据库
        RLock lock = redissonClient.getLock(String.format(SHORT_LINK_GOTO_LOCK_KEY, fullShortUrl));
        lock.lock();
        try {
            //获取到锁后，可能前面的线程已经查完数据库并更新缓存，所以先查一下缓存
            originalUrl = stringRedisTemplate.opsForValue().get(String.format(SHORT_LINK_GOTO_CACHE_KEY, fullShortUrl));
            if(StrUtil.isNotBlank(originalUrl)) {
                ((HttpServletResponse)response).sendRedirect(originalUrl);
                return;
            }

            //查数据库
            LambdaQueryWrapper<ShortLinkGotoDO> gotoWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class).eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO gotoDO = shortLinkGotoMapper.selectOne(gotoWrapper);
            if(gotoDO == null) {
                //TODO 需要进行风控
                stringRedisTemplate.opsForValue()
                        .set(
                                String.format(SHORT_LINK_GOTO_NULL_KEY, fullShortUrl),
                                "链接无效",
                                DEFAULT_NULL_CACHE_EXPIRE_TIME, TimeUnit.HOURS
                        );
                ((HttpServletResponse)response).sendRedirect("/page/notfound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, gotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(BaseDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableFlag, 1);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkWrapper);
            if(shortLinkDO != null) {
                Long timeLeft = ShortLinkUtil.getExpireTime(shortLinkDO);
                if(timeLeft < 0) {
                    stringRedisTemplate.opsForValue()
                            .set(
                                    String.format(SHORT_LINK_GOTO_NULL_KEY, fullShortUrl),
                                    "链接无效",
                                    DEFAULT_NULL_CACHE_EXPIRE_TIME, TimeUnit.HOURS
                            );
                    ((HttpServletResponse)response).sendRedirect("/page/notfound");
                    return;
                }
                stringRedisTemplate.opsForValue()
                        .set(
                                String.format(SHORT_LINK_GOTO_CACHE_KEY, shortLinkDO.getFullShortUrl()),
                                shortLinkDO.getOriginalUrl(),
                                timeLeft,
                                TimeUnit.MILLISECONDS
                        );
                ((HttpServletResponse)response).sendRedirect(shortLinkDO.getOriginalUrl());
            } else {
                stringRedisTemplate.opsForValue()
                        .set(
                                String.format(SHORT_LINK_GOTO_NULL_KEY, fullShortUrl),
                                "链接无效",
                                DEFAULT_NULL_CACHE_EXPIRE_TIME, TimeUnit.HOURS
                        );
                ((HttpServletResponse)response).sendRedirect("/page/notfound");
            }
        } finally {
            lock.unlock();
        }
    }

    private String getShortUrlSuffix(ShortLinkCreateReqDTO requestParam) {
        int tryTime = 0;
        String suffix = HashUtil.hashToBase62(requestParam.getOriginalUrl());
        while(true) {
            if(tryTime > MAX_GENERATE_SUFFIX_TRY_TIME) {
                throw new ServiceException("短链接生成冲突，请稍后重试！");
            }
            if(!shortLinkBloomFilter.contains(requestParam.getDomain() + '/' + suffix)) {
                break;
            }
            suffix = HashUtil.hashToBase62(requestParam.getOriginalUrl() + UUID.randomUUID());
            tryTime ++;
        }
        return suffix;
    }
}