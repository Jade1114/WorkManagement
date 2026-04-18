package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record AssignmentListItemResponse(
        Long id,
        Long courseId,
        Long teacherId,
        String title,
        String content,
        LocalDateTime deadline
) {
}
