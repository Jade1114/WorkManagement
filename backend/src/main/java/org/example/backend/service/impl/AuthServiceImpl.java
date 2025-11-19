package org.example.backend.service.impl;

import org.example.backend.common.exception.TokenInvalidException;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.AuthService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.UserVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 直接生成并返回 token（不再加入白名单）
        return jwtUtil.createToken(user.getId(), user.getRole());
    }

    @Override
    public void register(RegisterRequest req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));  // BCrypt 加密
        user.setRole("student");  // 注册的用户固定是学生

        userRepository.save(user);
    }

    @Override
    public Object getCurrentUser(String token) {

        Long userId = jwtUtil.getUserId(token);

        if (userId == null) {
            throw new TokenInvalidException("token无效或已过期");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());

        return vo;
    }
}
