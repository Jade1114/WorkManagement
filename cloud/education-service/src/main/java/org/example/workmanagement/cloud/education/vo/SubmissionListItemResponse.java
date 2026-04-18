package org.example.workmanagement.cloud.education.vo;

public record SubmissionListItemResponse(
        Long submissionId,
        Long assignmentId,
        Long studentId,
        String content,
        Integer score,
        String comment,
        Boolean graded
) {
}
