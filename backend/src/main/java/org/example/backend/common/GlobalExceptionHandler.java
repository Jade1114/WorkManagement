package org.example.backend.common;

import org.example.backend.common.exception.TokenInvalidException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理 RuntimeException （最常见）
    @ExceptionHandler(RuntimeException.class)
    public ApiResponse<?> handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(400, e.getMessage());
    }

    // token 校验失败，返回 401
    @ExceptionHandler(TokenInvalidException.class)
    public ApiResponse<?> handleTokenInvalidException(TokenInvalidException e) {
        return ApiResponse.error(401, e.getMessage());
    }

    // 处理参数校验异常（如果你未来使用 @Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.error(400, msg);
    }

    // 未知异常（兜底）
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        e.printStackTrace(); // 控制台打印方便调试
        return ApiResponse.error(500, "服务器内部错误");
    }
}
