package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record AssignmentListItemResponse(
        Long id,
        Long courseId,
        String courseTitle,
        Long teacherId,
        String title,
        String content,
        LocalDateTime deadline
) {
}
