package org.example.workmanagement.cloud.education.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionCreateRequest(
        @NotNull(message = "assignmentId 不能为空")
        Long assignmentId,
        @NotBlank(message = "提交内容不能为空")
        String content
) {
}
