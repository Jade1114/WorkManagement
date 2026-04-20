package org.example.workmanagement.cloud.education.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignmentCreateRequest {
    @NotNull
    private Long courseId;

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private LocalDateTime deadline;
}
