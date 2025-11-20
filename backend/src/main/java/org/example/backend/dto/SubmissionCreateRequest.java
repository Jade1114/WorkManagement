package org.example.backend.dto;

import lombok.Data;

@Data
public class SubmissionCreateRequest {
    private Long assignmentId;
    private String content;
}

