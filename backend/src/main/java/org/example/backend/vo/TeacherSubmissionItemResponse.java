package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TeacherSubmissionItemResponse {
    private Long submissionId;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String courseTitle;
    private Long studentId;
    private String studentName;
    private String assignmentContent;
    private String submitContent;
    private String submitTime;
    private Boolean graded;
    private Integer score;
    private String comment;
}
