package org.example.workmanagement.cloud.user.vo;

public record UserCheckResponse(Boolean exists, String role, Boolean active) {
}
