package org.example.backend.config;

import jakarta.annotation.Resource;
import org.example.backend.util.JwtUtil;
import org.example.backend.util.TokenResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private TokenResolver resolver;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler)
            throws Exception {

        jwtUtil.verifyToken(resolver.resolveToken(req));

        return true;
    }
}