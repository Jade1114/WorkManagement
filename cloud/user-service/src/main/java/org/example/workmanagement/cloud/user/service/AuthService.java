package org.example.workmanagement.cloud.user.service;

import org.example.workmanagement.cloud.user.entity.User;
import org.example.workmanagement.cloud.user.mapper.UserMapper;
import org.example.workmanagement.cloud.user.util.JwtTokenService;
import org.example.workmanagement.cloud.user.vo.LoginResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenService jwtTokenService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper, JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("用户名或密码错误");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new RuntimeException("账号已禁用");
        }

        String token = jwtTokenService.createToken(user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
