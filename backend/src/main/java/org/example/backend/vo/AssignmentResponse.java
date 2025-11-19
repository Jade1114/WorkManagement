package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class AssignmentResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private LocalDateTime deadline;
}
