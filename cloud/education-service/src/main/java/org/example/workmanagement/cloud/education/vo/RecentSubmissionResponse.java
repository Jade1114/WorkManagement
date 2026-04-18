package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record RecentSubmissionResponse(
        Long submissionId,
        String studentName,
        String assignmentTitle,
        String courseTitle,
        Boolean graded,
        Integer score,
        LocalDateTime submitTime
) {
}
