package org.example.workmanagement.cloud.gateway.util;

import org.example.workmanagement.cloud.gateway.config.JwtProperties;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public DecodedJWT verifyAndDecode(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("token 不能为空");
        }
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().isBlank()) {
            throw new RuntimeException("jwt secret 未配置");
        }

        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        return JWT.require(algorithm)
                .build()
                .verify(token);
    }

    public Long getUserId(DecodedJWT jwt) {
        return jwt.getClaim("userId").asLong();
    }

    public String getRole(DecodedJWT jwt) {
        return jwt.getClaim("role").asString();
    }
}
