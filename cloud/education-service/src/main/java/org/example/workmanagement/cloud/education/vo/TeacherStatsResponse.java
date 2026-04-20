package org.example.workmanagement.cloud.education.vo;

public record TeacherStatsResponse(
        long pendingSubmissions,
        long assignments,
        long students,
        long courses
) {
}
