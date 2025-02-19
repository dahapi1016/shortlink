package com.hapi.shortlink.admin.remote.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 短链接分页返回参数
 */
@Data
public class ShortLinkPageRespDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 短链接域名
     */
    private String domain;

    /**
     * 短链接地址
     */
    private String shortUri;

    /**
     * 完整短链接r
     */
    private String fullShortUrl;

    /**
     * 源地址
     */
    private String originalUrl;

    /**
     * 所属分组id
     */
    private String gid;

    /**
     * 有效期类型（0：永久有效，1：定期内有效）
     */
    private Integer hasExpiration;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationDate;

    /**
     * 原网址图标
     */
    private String favicon;


    /**
     * 短链接描述
     */
    private String describe;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
