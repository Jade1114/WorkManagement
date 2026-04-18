package org.example.workmanagement.cloud.education.vo;

public record CourseWithAssignmentCountResponse(
        Long id,
        String title,
        Long assignmentCount
) {
}
