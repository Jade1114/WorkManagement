package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DataScreenResponse {
    private List<CourseAssignmentsStat> assignmentsByCourse;
    private SubmissionStatusStat submissionStatus;
    private List<DailySubmissionStat> submissionsByDate;

    @Data
    @AllArgsConstructor
    public static class CourseAssignmentsStat {
        private String courseTitle;
        private long assignments;
    }

    @Data
    @AllArgsConstructor
    public static class SubmissionStatusStat {
        private long graded;
        private long pending;
    }

    @Data
    @AllArgsConstructor
    public static class DailySubmissionStat {
        private String date;
        private long count;
    }
}
