package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record RecentAssignmentResponse(
        Long id,
        Long courseId,
        String courseTitle,
        String title,
        LocalDateTime deadline,
        LocalDateTime createdAt
) {
}
