package org.example.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 登录接口
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest req) {
        String token = authService.login(req);

        HashMap<String, Object> data = new HashMap<>();
        data.put("token", token);

        return ApiResponse.success(data);
    }

    // 注册接口（学生）
    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ApiResponse.success("注册成功，请登录");
    }

    // 退出接口
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未携带 token");
        }

        return ApiResponse.success("退出成功");
    }

    // 获取当前用户
    @GetMapping("/me")
    public ApiResponse<?> me(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未携带 token");
        }

        String token = auth.substring(7);

        return ApiResponse.success(authService.getCurrentUser(token));
    }
}
