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

    @Column(updatable = false, insertable = false)
    private LocalDateTime createTime;

    @Column(updatable = false, insertable = false)
    private LocalDateTime updateTime;
}
