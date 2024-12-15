package com.hapi.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {

    /**
     *
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户响应实体
     */
    UserRespDTO getUserByUsername(String username);
}
