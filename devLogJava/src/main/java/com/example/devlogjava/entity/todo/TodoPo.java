package com.example.devlogjava.entity.todo;

import lombok.Data;

@Data
public class TodoPo {
    private String id;
    private String title;
    private String status; // pending / done
    private String userId;
    private String createdAt;
    private String updatedAt;
}
