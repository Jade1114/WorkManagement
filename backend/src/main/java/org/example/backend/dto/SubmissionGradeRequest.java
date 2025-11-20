package org.example.backend.dto;

import lombok.Data;

@Data
public class SubmissionGradeRequest {
    private Long submissionId;
    private Integer score;
    private String comment;
}


