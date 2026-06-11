package com.example.devlogjava.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPo {
    private String id;

    private String phone;
    
    private String password;
    
    private String nickname;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
