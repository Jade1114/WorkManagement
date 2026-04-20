package org.example.workmanagement.cloud.user.dto;

import jakarta.validation.constraints.Pattern;

public record AdminUpdateUserRequest(
        @Pattern(regexp = "admin|teacher|student", message = "用户角色不合法")
        String role,
        Boolean active
) {
}
