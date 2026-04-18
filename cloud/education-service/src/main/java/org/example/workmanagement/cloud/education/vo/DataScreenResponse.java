package org.example.workmanagement.cloud.education.vo;

import java.util.List;

public record DataScreenResponse(
        List<CourseAssignmentsStat> assignmentsByCourse,
        SubmissionStatusStat submissionStatus,
        List<DailySubmissionStat> submissionsByDate
) {
    public record CourseAssignmentsStat(String courseTitle, long assignments) {
    }

    public record SubmissionStatusStat(long graded, long pending) {
    }

    public record DailySubmissionStat(String date, long count) {
    }
}
