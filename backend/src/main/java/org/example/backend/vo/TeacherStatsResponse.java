package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TeacherStatsResponse {
    private long pendingSubmissions; // 待评分提交数
    private long assignments;        // 已发布作业数
    private long students;           // 学生总数
    private long courses;            // 学科/课程总数
}
