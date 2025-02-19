package com.hapi.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 用户注册请求入参
 */
@Data
public class UserRegisterReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 真名
     */
    private String realName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 手机号
     */
    private String phone;
}
