package com.hapi.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.common.convention.result.Results;
import com.hapi.shortlink.admin.dto.req.UserLoginReqDTO;
import com.hapi.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.hapi.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.hapi.shortlink.admin.dto.resp.ActualUserRespDTO;
import com.hapi.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名返回脱敏后用户信息
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByName (@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 返回实际用户信息（未脱敏）
     */
    @GetMapping("/api/short-link/admin/v1/user/actual/{username}")
    public Result<ActualUserRespDTO> getActualUserByName(@PathVariable("username") String username) {
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), ActualUserRespDTO.class));
    }

    /**
     * 查找用户名是否存在
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUserName(@RequestParam String username) {
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 用户注册
     * @param requestParam 用户信息表单
     */
    @PostMapping("/api/short-link/admin/v1/user/register")
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 修改用户信息
     * @param requestParam 用户信息表单
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录接口
     * @param requestParam 登录信息表单
     * @return 用户token
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录
     * @param username 用户名
     * @param token token
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login/")
    public Result<Boolean> isLogged(@RequestParam String username, @RequestParam String token) {
        return Results.success(userService.isLogged(username, token));
    }

    /**
     * 退出登录
     * @param username 用户名
     * @param token 登陆凭证
     */
    @PostMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logOut(@RequestParam String username, @RequestParam String token) {
        userService.logOut(username, token);
        return Results.success();
    }
}
