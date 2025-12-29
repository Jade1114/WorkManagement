package org.example.backend.service;

import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.WechatBindRequest;
import org.example.backend.dto.WechatLoginRequest;
import org.example.backend.vo.LoginResponse;
import org.example.backend.vo.WechatLoginResult;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);

    WechatLoginResult wechatLogin(WechatLoginRequest request);

    LoginResponse wechatBind(WechatBindRequest request);
}
