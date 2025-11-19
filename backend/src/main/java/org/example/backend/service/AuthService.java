package org.example.backend.service;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.vo.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);
}
