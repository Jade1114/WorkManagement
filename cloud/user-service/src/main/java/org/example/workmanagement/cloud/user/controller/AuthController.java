package org.example.workmanagement.cloud.user.controller;

import org.example.workmanagement.cloud.user.common.ApiResponse;
import org.example.workmanagement.cloud.user.dto.LoginRequest;
import org.example.workmanagement.cloud.user.service.AuthService;
import org.example.workmanagement.cloud.user.vo.LoginResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request.getUsername(), request.getPassword()));
    }
}
