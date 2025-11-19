package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment")
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属课程
    private Long courseId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime deadline;
}

