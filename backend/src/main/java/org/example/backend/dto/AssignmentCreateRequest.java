package org.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentCreateRequest {
    private Long courseId;
    private String title;
    private String content;
    private LocalDateTime deadline;
}

