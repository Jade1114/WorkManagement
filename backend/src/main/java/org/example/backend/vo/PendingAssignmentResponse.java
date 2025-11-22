package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未提交作业列表数据（学生视角）
 */
@AllArgsConstructor
@Data
public class PendingAssignmentResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String content;
    private LocalDateTime deadline;
}
