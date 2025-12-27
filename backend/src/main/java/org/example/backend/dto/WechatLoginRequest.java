package org.example.backend.dto;

import lombok.Data;

@Data
public class WechatLoginRequest {
    private String code;
    private String studentNo; // 可选：若需要学号校验绑定
}
