package com.hapi.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hapi.shortlink.admin.dao.entity.UserDO;
import com.hapi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.hapi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.UserLoginRespDTO;
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
     * @param requestParam 用户注册请求表单
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 更改用户信息
     * @param requestParam 更改信息请求表单
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     * @param requestParam 登录信息表单
     * @return 用户登录凭证（token）
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否已经登录
     * @param username 用户名
     * @param token 用户登录凭证
     */
    boolean isLogged(String username, String token);

    /**
     * 退出登录
     * @param username 用户名
     * @param token 用户登录凭证
     */
    void logOut(String username, String token);
}
