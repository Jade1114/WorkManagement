package org.example.backend.dto;

import lombok.Data;

@Data
public class UpdateCoursesRequest {
    private Long id;
    private String title;
}
