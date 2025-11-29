package org.example.backend.service;

import org.example.backend.vo.UserResponse;
import org.example.backend.vo.AdminUserResponse;
import org.example.backend.dto.AdminUpdateUserRequest;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser(Long userId);

    String changePassword(Long userId, String oldPassword, String newPassword);

    List<UserResponse> getAllUsers();

    List<AdminUserResponse> getAllForAdmin();

    void updateUser(AdminUpdateUserRequest req);
}
