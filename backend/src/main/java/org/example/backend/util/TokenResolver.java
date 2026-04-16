package org.example.backend.util;

import org.example.backend.common.exception.TokenInvalidException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TokenResolver {
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new TokenInvalidException("未携带 token 或格式错误");
        }

        return header.substring(7).trim();

    }
}