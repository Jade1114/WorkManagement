package org.example.backend.service;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.vo.LoginResponse;

public interface AuthService {

    // 登录，返回 token
    LoginResponse login(LoginRequest request);

    // 注册（学生）
    void register(RegisterRequest request);
}
