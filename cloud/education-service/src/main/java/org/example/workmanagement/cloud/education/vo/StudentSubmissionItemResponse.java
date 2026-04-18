package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record StudentSubmissionItemResponse(
        Long submissionId,
        Long assignmentId,
        String content,
        Integer score,
        String comment,
        Boolean graded,
        LocalDateTime submitTime
) {
}
