package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AdminUserResponse {
    private Long id;
    private String username;
    private String role;
    private Boolean active;
}
