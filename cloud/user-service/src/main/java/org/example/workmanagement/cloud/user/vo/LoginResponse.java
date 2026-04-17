package org.example.workmanagement.cloud.user.vo;

public record LoginResponse(
        String token,
        Long userId,
        String username,
        String role
) {
}
