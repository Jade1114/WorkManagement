package org.example.workmanagement.cloud.education.vo;

import java.time.LocalDateTime;

public record TopSubmitterResponse(
        Long studentId,
        String studentName,
        Long count,
        LocalDateTime lastSubmit
) {
}
