package org.example.workmanagement.cloud.user.controller;

import java.util.List;

import org.example.workmanagement.cloud.user.common.ApiResponse;
import org.example.workmanagement.cloud.user.dto.AdminUpdateUserRequest;
import org.example.workmanagement.cloud.user.dto.ChangePasswordRequest;
import org.example.workmanagement.cloud.user.service.UserManagementService;
import org.example.workmanagement.cloud.user.vo.AdminUserResponse;
import org.example.workmanagement.cloud.user.vo.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.success(userManagementService.getCurrentUser(userId));
    }

    @PutMapping("/me/password")
    public ApiResponse<String> changePassword(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ApiResponse.success(userManagementService.changePassword(userId, request));
    }

    @GetMapping(params = "role=student")
    public ApiResponse<List<UserResponse>> students(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(userManagementService.listStudents(userId, userRole));
    }

    @GetMapping
    public ApiResponse<List<AdminUserResponse>> adminList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole) {
        return ApiResponse.success(userManagementService.listUsersForAdmin(userId, userRole));
    }

    @PatchMapping("/{targetUserId}")
    public ApiResponse<String> adminUpdate(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long targetUserId,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        userManagementService.updateUser(userId, userRole, targetUserId, request);
        return ApiResponse.success("更新成功");
    }
}
