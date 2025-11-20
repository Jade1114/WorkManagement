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

    private Long assignmentId;   // 所属作业 ID
    private Long studentId;      // 提交人 ID

    @Column(columnDefinition = "TEXT")
    private String content;      // 学生提交内容

    private Integer score;       // 老师评分（可为空）
    private String comment;      // 老师评语（可为空）

    private Boolean graded;      // 是否已批改（true/false）
}