package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubmissionListItemResponse {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private String content;
    private Integer score;
    private Boolean graded;
}

