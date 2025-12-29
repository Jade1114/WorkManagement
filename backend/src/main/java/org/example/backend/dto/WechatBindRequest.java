package org.example.backend.dto;

import lombok.Data;

@Data
public class WechatBindRequest {
    private String bindTicket;
    private String username;
    private String password;
}
