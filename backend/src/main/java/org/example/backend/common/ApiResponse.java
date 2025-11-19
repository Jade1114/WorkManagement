package org.example.backend.common;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer code;
    private String message;
    private T data;

    // 成功（无数据）
    public static <T> ApiResponse<T> success() {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(null);
        return resp;
    }

    // 成功（仅消息）
    public static <T> ApiResponse<T> success(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(200);
        resp.setMessage(message);
        resp.setData(null);
        return resp;
    }

    // 成功（携带数据）
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(200);
        resp.setMessage("success");
        resp.setData(data);
        return resp;
    }

    // 成功（自定义消息 + 数据）
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(200);
        resp.setMessage(message);
        resp.setData(data);
        return resp;
    }

    // 错误(自定义消息+ 错误代码)
    public static <T> ApiResponse<T> error(Integer code, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.setCode(code);
        resp.setMessage(message);
        resp.setData(null);
        return resp;
    }
}