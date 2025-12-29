package org.example.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatLoginResult {
    private Boolean needBind;
    private String bindTicket;
    private Integer expireSeconds;
    private String token;
    private Long userId;
    private String username;
    private String role;
}
