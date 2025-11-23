package org.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

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
}