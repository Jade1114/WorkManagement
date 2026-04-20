package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record StudentSubmissionItemResponse(
        Long submissionId,
        Long assignmentId,
        String assignmentTitle,
        Long courseId,
        String courseTitle,
        String content,
        String submitContent,
        String comment,
        Boolean graded,
        Integer score,
        LocalDateTime submitTime
) {
}
