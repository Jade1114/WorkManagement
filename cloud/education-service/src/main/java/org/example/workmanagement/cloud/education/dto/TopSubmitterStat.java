package org.example.workmanagement.cloud.education.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TopSubmitterStat {
    private Long studentId;
    private Long count;
    private LocalDateTime lastSubmit;
}
