package org.example.workmanagement.cloud.education.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Submission {
    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String content;
    private Integer score;
    private String comment;
    private Boolean graded;
    private LocalDateTime submitTime;
}
