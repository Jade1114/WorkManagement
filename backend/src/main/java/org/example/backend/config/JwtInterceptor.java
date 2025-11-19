package org.example.backend.config;

import jakarta.annotation.Resource;
import org.example.backend.common.exception.TokenInvalidException;
import org.example.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception {

        // 获取 Authorization 头
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new TokenInvalidException("未携带 token 或格式错误");
        }

        // 提取 token
        String token = authHeader.substring(7);

        // 校验 token 是否有效（过期、无效都会解析失败）
        jwtUtil.verifyToken(token);

        return true;
    }
}