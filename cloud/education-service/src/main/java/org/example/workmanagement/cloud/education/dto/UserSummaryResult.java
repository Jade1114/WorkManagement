package org.example.workmanagement.cloud.education.dto;

public record UserSummaryResult(
        Long id,
        String username,
        String role,
        Boolean active
) {
}
