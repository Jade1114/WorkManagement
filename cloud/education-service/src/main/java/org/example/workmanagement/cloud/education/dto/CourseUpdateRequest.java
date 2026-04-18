package org.example.workmanagement.cloud.education.dto;

import jakarta.validation.constraints.NotNull;

public record CourseUpdateRequest(
        @NotNull(message = "课程ID不能为空")
        Long id,
        String title
) {
}
