package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record PendingAssignmentItemResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        String content,
        LocalDateTime deadline
) {
}
