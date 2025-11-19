package org.example.backend.controller;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.backend.common.ApiResponse;
import org.example.backend.dto.ChangePasswordRequest;
import org.example.backend.service.UserService;
import org.example.backend.util.JwtUtil;
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

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserId(token);
        return ApiResponse.success(userService.getCurrentUser(userId));
    }


    @PutMapping("/changePassword")
    public ApiResponse<String> changePassword(
            HttpServletRequest request,
            @RequestBody ChangePasswordRequest req
    ) {

        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtUtil.getUserId(token);

        return ApiResponse.success(
                userService.changePassword(userId, req.getOldPassword(), req.getNewPassword())
        );
    }

    @GetMapping("/students")
    public ApiResponse<List<UserResponse>> getAllStudents() {
        return ApiResponse.success(userService.getAllUsers());
    }
}
