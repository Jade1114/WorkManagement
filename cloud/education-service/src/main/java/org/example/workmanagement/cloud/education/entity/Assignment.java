package org.example.workmanagement.cloud.education.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Assignment {
    private Long id;
    private Long courseId;
    private Long teacherId;
    private String title;
    private String content;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
}
