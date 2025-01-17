package com.hapi.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 用户DO类
 */
@Data
@TableName("t_user")
public class UserDO  {

    @TableId(type = IdType.AUTO)
    /*
      idr
     */
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

    /**
     * 注销时间戳
     */
    private Long deletionTime;

    /**
     * 注册时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 注销标志位，0：未注销，1：已注销
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

    public UserDO() {}
}