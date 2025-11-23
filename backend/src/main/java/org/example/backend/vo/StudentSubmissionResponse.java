package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StudentSubmissionResponse {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseTitle;
    private String submitContent;
    private String comment;
    private Boolean graded;
    private Integer score;
}
