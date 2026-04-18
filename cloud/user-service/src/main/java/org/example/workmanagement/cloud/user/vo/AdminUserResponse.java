package org.example.workmanagement.cloud.user.vo;

public record AdminUserResponse(
        Long id,
        String username,
        String role,
        Boolean active
) {
}
