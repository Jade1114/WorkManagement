package org.example.backend.common.exception;

/**
 * 自定义异常：表示 token 无效或过期
 */
public class TokenInvalidException extends RuntimeException {

    public TokenInvalidException(String message) {
        super(message);
    }
}
