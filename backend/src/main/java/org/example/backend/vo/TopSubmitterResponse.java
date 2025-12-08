package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TopSubmitterResponse {
    private Long studentId;
    private String studentName;
    private Long count;
    private LocalDateTime lastSubmit;
}
