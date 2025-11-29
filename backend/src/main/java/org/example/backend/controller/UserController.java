package org.example.backend.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.ChangePasswordRequest;
import org.example.backend.dto.AdminUpdateUserRequest;
import org.example.backend.service.UserService;
import org.example.backend.util.JwtUtil;
import org.example.backend.vo.AdminUserResponse;
import org.example.backend.vo.UserResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private JwtUtil jwtUtil;

    // 用户获取自己的信息
    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpServletRequest request) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    // 用户更改密码
    @PutMapping("/changePassword")
    public ApiResponse<String> changePassword(HttpServletRequest request,
                                              @RequestBody ChangePasswordRequest req) {
        Long userId = jwtUtil.getUserId(request);
        return ApiResponse.success(
                userService.changePassword(userId, req.getOldPassword(), req.getNewPassword())
        );
    }


    // 老师获取所有学生列表
    @GetMapping("/students")
    public ApiResponse<List<UserResponse>> getAllStudents(HttpServletRequest request) {
        String role = jwtUtil.getRole(request);
        if (!"teacher".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(userService.getAllUsers());
    }

    // 管理员获取所有用户列表
    @GetMapping("/admin/list")
    public ApiResponse<List<AdminUserResponse>> adminList(HttpServletRequest request) {
        String role = jwtUtil.getRole(request);
        if (!"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        return ApiResponse.success(userService.getAllForAdmin());
    }

    // 管理员更新用户角色/状态
    @PutMapping("/admin/update")
    public ApiResponse<String> adminUpdate(HttpServletRequest request,
                                           @RequestBody AdminUpdateUserRequest req) {
        String role = jwtUtil.getRole(request);
        if (!"admin".equals(role)) {
            throw new RuntimeException("权限不足");
        }
        userService.updateUser(req);
        return ApiResponse.success("更新成功");
    }
}
