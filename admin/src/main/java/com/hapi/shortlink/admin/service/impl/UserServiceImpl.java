package com.hapi.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.admin.common.convention.exception.ClientException;
import com.hapi.shortlink.admin.common.convention.exception.ServiceException;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dao.mapper.UserMapper;
import com.hapi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.hapi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.hapi.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.hapi.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.hapi.shortlink.admin.common.convention.errorcode.UserErrorCode.*;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegistrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        if(userDO == null) {
            throw new ServiceException(USER_NOT_FOUND);
        }

        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    @Override
    public boolean hasUserName(String username) {
        return userRegistrationBloomFilter.contains(username);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(hasUserName(requestParam.getUsername())) {
            throw new ServiceException(USER_NAME_EXIST_ERROR);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        boolean lockAcquired = lock.tryLock();
        if (lockAcquired) {
            try {
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
                if(inserted < 1) {
                    throw new ServiceException(USER_SAVE_ERROR);
                }
                userRegistrationBloomFilter.add(requestParam.getUsername());
                return;
            } finally {
                lock.unlock();
            }
        }
        throw new ServiceException(USER_NAME_EXIST_ERROR);
    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        //TODO 检查要修改的用户名是否为已登录的用户名
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class).
                eq(UserDO::getUsername, requestParam.getUsername());
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0L);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        //判断用户名和密码是否正确
        if(userDO == null) {
            throw new ServiceException(USER_NOT_FOUND);
        }

        Boolean hasLogged = stringRedisTemplate.hasKey(USER_LOGIN_KEY + userDO.getUsername());
        //不能重复登录
        if(Boolean.TRUE.equals(hasLogged)) {
            throw new ClientException(USER_ALREADY_LOGGED_ERROR);
        }

        String userToken = UUID.randomUUID().toString();
        String userInfoJSON = JSON.toJSONString(userDO);
        stringRedisTemplate.opsForHash().put(USER_LOGIN_KEY + userDO.getUsername(), userToken, userInfoJSON);
        return new UserLoginRespDTO(userToken);
    }

    @Override
    public boolean isLogged(String username, String token) {
        return stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token) != null;
    }
}
