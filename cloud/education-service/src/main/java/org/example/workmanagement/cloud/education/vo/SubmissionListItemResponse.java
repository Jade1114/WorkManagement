package org.example.workmanagement.cloud.education.vo;

public record SubmissionListItemResponse(
        Long id,
        Long submissionId,
        Long assignmentId,
        Long studentId,
        String username,
        String studentUsername,
        String content,
        Integer score,
        String comment,
        Boolean graded
) {
}
