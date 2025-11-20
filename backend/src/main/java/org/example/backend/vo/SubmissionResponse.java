package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubmissionResponse {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String content;
    private Integer score;
    private String comment;
    private Boolean graded;
}


