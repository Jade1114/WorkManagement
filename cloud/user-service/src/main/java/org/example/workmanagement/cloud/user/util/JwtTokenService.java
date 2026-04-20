package org.example.workmanagement.cloud.user.util;

import java.util.Date;

import org.example.workmanagement.cloud.user.config.JwtProperties;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JwtTokenService {

    private final JwtProperties jwtProperties;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String createToken(Long userId, String role) {
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        long expireMs = jwtProperties.getExpireMs() == null ? 3600000L : jwtProperties.getExpireMs();
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + expireMs))
                .sign(algorithm);
    }
}
