package com.hapi.shortlink.admin.dto.resp;

import lombok.Data;

@Data
public class ActualUserRespDTO {


    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 手机号
     */
    private String phone;
}
