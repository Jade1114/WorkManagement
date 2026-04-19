package org.example.workmanagement.cloud.education.dto;

import jakarta.validation.constraints.NotNull;

public record SubmissionGradeRequest(
        @NotNull(message = "score 不能为空")
        Integer score,
        String comment
) {
}
