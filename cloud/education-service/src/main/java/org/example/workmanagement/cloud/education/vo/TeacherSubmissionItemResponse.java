package org.example.workmanagement.cloud.education.vo;

public record TeacherSubmissionItemResponse(
        Long submissionId,
        Long assignmentId,
        String assignmentTitle,
        Long courseId,
        String courseTitle,
        Long studentId,
        String studentName,
        String assignmentContent,
        String submitContent,
        String submitTime,
        Boolean graded,
        Integer score,
        String comment
) {
}
