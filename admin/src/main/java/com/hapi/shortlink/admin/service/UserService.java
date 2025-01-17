package com.hapi.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
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

    /**
     * 查询对应的用户名是否已经存在
     * @param username 用户名
     * @return 是否存在
     */
    boolean hasUserName(String username);

    /**
     * 注册用户
     * @param requestParam 用户注册请求参数
     */
    void register(UserRegisterReqDTO requestParam);
}
