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

        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new TokenInvalidException("未携带 token 或格式错误");
        }

        String token = header.substring(7).trim();

        jwtUtil.verifyToken(token);

        return true;
    }
}