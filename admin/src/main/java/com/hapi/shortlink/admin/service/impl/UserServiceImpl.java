package com.hapi.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hapi.shortlink.admin.common.convention.errorcode.UserErrorCode;
import com.hapi.shortlink.admin.common.convention.exception.ServiceException;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dao.mapper.UserMapper;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户接口实现层
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(queryWrapper);

        if(userDO == null) {
            throw new ServiceException(UserErrorCode.USER_NOT_FOUND);
        }

        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }
}
