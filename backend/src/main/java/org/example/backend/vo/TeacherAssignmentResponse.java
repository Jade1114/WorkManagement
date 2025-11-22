package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class TeacherAssignmentResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String content;
    private LocalDateTime deadline;
}
