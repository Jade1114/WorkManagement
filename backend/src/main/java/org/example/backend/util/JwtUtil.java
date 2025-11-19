package org.example.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.backend.common.exception.TokenInvalidException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "abcdefghijk1234567890ABCDEFGH"; // 建议更复杂
    // token 过期时间：1小时
    private static final long EXPIRE_MS = 1000 * 60 * 60;  // 1h = 3600000ms

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    // 创建 token
    public String createToken(Long userId, String role) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_MS))
                .sign(algorithm);
    }

    // 校验 token（过期/伪造）
    public DecodedJWT verifyToken(String token) {
        try {
            return JWT.require(algorithm).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenInvalidException("token 无效或已过期");
        }
    }

    // 获取 userId
    public Long getUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt != null ? jwt.getClaim("userId").asLong() : null;
    }

    // 获取 role
    public String getRole(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt != null ? jwt.getClaim("role").asString() : null;
    }
}