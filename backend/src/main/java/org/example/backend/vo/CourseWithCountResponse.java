package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CourseWithCountResponse {
    private Long id;
    private String title;
    private Long assignmentCount;
}
