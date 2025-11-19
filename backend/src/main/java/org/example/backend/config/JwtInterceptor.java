package org.example.backend.config;

import org.example.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception {

        String uri = req.getRequestURI();

        // 放行 Auth 接口（登录、注册、刷新等）
        if (uri.startsWith("/api/auth")) {
            return true;
        }

        // 获取 Authorization Bearer token
        String authHeader = req.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(401);
            return false;
        }

        String token = authHeader.substring(7).trim();

        // 校验 token（是否过期/伪造）
        if (jwtUtil.verifyToken(token) == null) {
            resp.setStatus(401);
            return false;
        }

        return true;
    }
}