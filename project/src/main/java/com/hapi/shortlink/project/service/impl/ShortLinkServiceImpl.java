package com.hapi.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.project.common.convention.exception.ServiceException;
import com.hapi.shortlink.project.dao.entity.BaseDO;
import com.hapi.shortlink.project.dao.entity.ShortLinkDO;
import com.hapi.shortlink.project.dao.mapper.ShortLinkMapper;
import com.hapi.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.hapi.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.hapi.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.hapi.shortlink.project.service.ShortLinkService;
import com.hapi.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private static final int MAX_GENERATE_SUFFIX_TRY_TIME = 10;
    private final RBloomFilter<String> shortLinkBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortUrlSuffix = getShortUrlSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);

        String fullShortUrl = requestParam.getDomain() + '/' + shortUrlSuffix;

        shortLinkDO.setFullShortUrl(fullShortUrl);
        shortLinkDO.setShortUri(shortUrlSuffix);
        try {
            baseMapper.insert(shortLinkDO);
        } catch (DuplicateKeyException e) {
            throw new ServiceException(String.format("短链接：%s 重复", shortLinkDO.getFullShortUrl()));
        }

        shortLinkBloomFilter.add(fullShortUrl);

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