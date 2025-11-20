package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.AuthService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.LoginResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


    @Resource
    private UserRepository userRepository;

    @Resource
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void register(RegisterRequest req) {

        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new RuntimeException("密码不能为空");
        }

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));  // BCrypt 加密
        user.setRole("student");  // 注册的用户固定是学生

        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw e; // 由全局异常捕捉转为500 服务器内部错误
        }
    }

    @Override
    public LoginResponse login(LoginRequest req) {

        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 1. 查询用户是否存在
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 校验密码
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 生成 token
        String token = jwtUtil.createToken(user.getId(), user.getRole());

        // 4. 返回 LoginResponse
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }
}
