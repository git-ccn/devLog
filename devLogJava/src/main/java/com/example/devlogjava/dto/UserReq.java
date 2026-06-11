package com.example.devlogjava.dto;

import lombok.Data;

@Data
public class UserReq {
    private String phone;
    private String password;
    private String email;
    private String code; // 验证码，用于注册或找回密码
    private String uuid; // 用于 Redis 中标识验证码的唯一 key
}
