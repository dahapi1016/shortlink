package com.hapi.shortlink.project.dto.req;

import lombok.Data;

import java.util.Date;

@Data
public class ShortLinkCreateReqDTO {

    /**
     * 短链接域名
     */
    private String domain;

    /**
     * 源地址
     */
    private String originalUrl;

    /**
     * 所属分组id
     */
    private String gid;

    /**
     * 创建人用户名
     */
    private String username;

    /**
     * 启用标识（0：未启用，1：启用）
     */
    private Integer enableFlag;

    /**
     * 创建方式（0：接口创建，1：控制台创建）
     */
    private Integer createdBy;

    /**
     * 有效期类型（0：永久有效，1：定期内有效）
     */
    private Integer hasExpiration;

    /**
     * 过期时间
     */
    private Date expirationDate;

    /**
     * 短链接描述
     */
    private String describe;
}
