package org.example.backend.controller;

import jakarta.annotation.Resource;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.service.AuthService;
import org.example.backend.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Resource
    private JwtUtil  jwtUtil;

    // 注册接口
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ApiResponse.success("注册成功，请登录");
    }

    // 登录接口
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest req) {
        return ApiResponse.success(authService.login(req));
    }

    // 退出接口
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success("退出成功");
    }
}
