package org.example.backend.service.impl;

import jakarta.annotation.Resource;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.UserService;
import org.example.backend.vo.UserResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public String changePassword(Long userId, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 校验旧密码
        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 3. 修改为新密码（必须加密后保存）
        user.setPassword(encoder.encode(newPassword));

        // 4. 保存到数据库
        userRepository.save(user);

        return "密码修改成功";
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> list = userRepository.findAll();

        return list.stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole()))
                .collect(Collectors.toList());
    }
}
