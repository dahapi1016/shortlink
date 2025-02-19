package com.hapi.shortlink.admin.common.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static com.hapi.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * 用户信息传输过滤器
 *
 */
@AllArgsConstructor
public class UserTransmitFilter implements Filter {

    StringRedisTemplate stringRedisTemplate;

    private static final List<String> IgnoreURI = Lists.newArrayList(
            "/api/short-/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username",
            "/api/short-link/admin/v1/user/register"
    );

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        if (!IgnoreURI.contains(requestURI)) {
            String userName = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            Object userInfoJSON = stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + userName, token);
            if(userInfoJSON != null) {
                UserContext.setUser(JSON.parseObject(userInfoJSON.toString(), UserInfoDTO.class));
            }
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}