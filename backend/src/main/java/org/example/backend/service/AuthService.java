package org.example.backend.service;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;

public interface AuthService {

    // 登录，返回 token
    String login(LoginRequest request);

    // 注册（学生）
    void register(RegisterRequest request);

    // 获取当前用户
    Object getCurrentUser(String token);
}
