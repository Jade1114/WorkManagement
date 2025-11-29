package org.example.backend.dto;

import lombok.Data;

@Data
public class AdminUpdateUserRequest {
    private Long userId;
    private String role;
    private Boolean active;
}
