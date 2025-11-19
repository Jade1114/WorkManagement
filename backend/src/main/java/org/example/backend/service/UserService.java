package org.example.backend.service;

import org.example.backend.vo.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser(Long userId);

    String changePassword(Long userId, String oldPassword, String newPassword);

    List<UserResponse> getAllUsers();
}
