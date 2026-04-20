package org.example.workmanagement.cloud.education.vo;

public record SubmissionResponse(
        Long id,
        Long assignmentId,
        Long studentId,
        String content,
        Integer score,
        String comment,
        Boolean graded
) {
}
