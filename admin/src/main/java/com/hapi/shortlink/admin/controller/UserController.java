package com.hapi.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.common.convention.result.Results;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.hapi.shortlink.admin.dto.resp.ActualUserRespDTO;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名返回脱敏后用户信息
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByName (@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }
    /**
     * 返回实际用户信息（未脱敏）
     */
    @GetMapping("/api/short-link/v1/user/actual/{username}")
    public Result<ActualUserRespDTO> getActualUserByName(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), ActualUserRespDTO.class));
    }

    /**
     * 查找用户名是否存在
     */
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUserName(@RequestParam String username) {
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 用户注册
     * @param requestParam 用户信息表单
     */
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }
}
