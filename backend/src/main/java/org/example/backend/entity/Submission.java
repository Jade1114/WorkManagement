package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Data
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long assignmentId;
    private Long studentId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer score;
    private String comment;

    private Boolean graded;

    // 提交时间，用于按最近提交排序
    private LocalDateTime submitTime;

    @PrePersist
    public void prePersist() {
        if (submitTime == null) {
            submitTime = LocalDateTime.now();
        }
    }
}
