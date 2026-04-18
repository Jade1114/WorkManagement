package org.example.workmanagement.cloud.user.dto;

import jakarta.validation.constraints.NotNull;

public record AdminUpdateUserRequest(
        @NotNull(message = "userId 不能为空")
        Long userId,
        String role,
        Boolean active
) {
}
