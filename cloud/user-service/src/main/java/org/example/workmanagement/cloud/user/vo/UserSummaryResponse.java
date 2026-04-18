package org.example.workmanagement.cloud.user.vo;

public record UserSummaryResponse(
        Long id,
        String username,
        String role,
        Boolean active
) {
}
