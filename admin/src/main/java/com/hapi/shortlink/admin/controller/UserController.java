package com.hapi.shortlink.admin.controller;

import com.hapi.shortlink.admin.common.convention.result.Result;
import com.hapi.shortlink.admin.common.convention.result.Results;
import com.hapi.shortlink.admin.dto.resp.UserRespDTO;
import com.hapi.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 根据用户名返回用户信息
     */
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByName (@PathVariable("username") String username) {
        return Results.success(userService.getUserByUsername(username));
    }
}
