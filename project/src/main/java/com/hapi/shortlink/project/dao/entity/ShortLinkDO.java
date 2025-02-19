package com.hapi.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
 * t_link表实体类
 * @author hapi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_link")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShortLinkDO extends BaseDO{

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
     * 创建人用户名
     */
    private String username;

    /**
     * 点击量
     */
    private Integer clickNum;

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
     * 原网址图标
     */
    private String favicon;

    /**
     * 短链接描述
     */
    @TableField("`describe`")
    private String describe;
}