package org.example.workmanagement.cloud.education.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseUpdateRequest(
        @NotBlank(message = "课程标题不能为空")
        String title
) {
}
