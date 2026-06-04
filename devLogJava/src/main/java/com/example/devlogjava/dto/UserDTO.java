package com.example.devlogjava.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String phone;
    private String password;
    private String code; // 验证码，用于注册或找回密码
}
