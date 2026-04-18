package org.example.workmanagement.cloud.user.service;

import org.example.workmanagement.cloud.user.common.BusinessException;
import org.example.workmanagement.cloud.user.entity.User;
import org.example.workmanagement.cloud.user.mapper.UserMapper;
import org.example.workmanagement.cloud.user.util.JwtTokenService;
import org.example.workmanagement.cloud.user.vo.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserMapper userMapper;
    private final JwtTokenService jwtTokenService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserMapper userMapper, JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(String username, String password) {
        log.info("login start: username={}", username);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            log.warn("login failed: username={}, reason=blank credentials", username);
            throw new BusinessException("用户名或密码错误");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("login failed: username={}, reason=user not found", username);
            throw new BusinessException("用户名或密码错误");
        }

        if (!encoder.matches(password, user.getPassword())) {
            log.warn("login failed: username={}, reason=password mismatch", username);
            throw new BusinessException("用户名或密码错误");
        }

        if (!Boolean.TRUE.equals(user.getActive())) {
            log.warn("login failed: username={}, userId={}, reason=account disabled", username, user.getId());
            throw new BusinessException("账号已禁用");
        }

        String token = jwtTokenService.createToken(user.getId(), user.getRole());
        log.info("login success: userId={}, role={}", user.getId(), user.getRole());
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
