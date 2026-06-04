package com.example.devlogjava.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String phone;
    
    private String password;
    
    private String nickname;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
