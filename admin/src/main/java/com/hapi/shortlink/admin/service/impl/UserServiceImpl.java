package com.hapi.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.admin.common.convention.exception.ServiceException;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dao.mapper.UserMapper;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.hapi.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.hapi.shortlink.admin.common.convention.errorcode.UserErrorCode.*;

/**
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegistrationBloomFilter;
    private final RedissonClient redissonClient;

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
}
