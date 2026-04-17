package org.example.workmanagement.cloud.user.controller;

import java.util.Map;

import org.example.workmanagement.cloud.user.config.JwtProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/config")
public class InternalConfigController {

    private final JwtProperties jwtProperties;

    public InternalConfigController(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @GetMapping("/jwt")
    public Map<String, Object> jwtConfig() {
        String secret = jwtProperties.getSecret();
        Long expireMs = jwtProperties.getExpireMs();
        return Map.of(
                "secretLoaded", secret != null && !secret.isBlank(),
                "secretLength", secret == null ? 0 : secret.length(),
                "expireMs", expireMs
        );
    }
}
