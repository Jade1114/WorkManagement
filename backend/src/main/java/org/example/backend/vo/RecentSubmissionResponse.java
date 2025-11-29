package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class RecentSubmissionResponse {
    private Long submissionId;
    private String studentName;
    private String assignmentTitle;
    private String courseTitle;
    private Boolean graded;
    private Integer score;
    private LocalDateTime submitTime;
}
