package com.example.devlogjava.entity.note;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NotePo {
    private String id;

    private String title;

    private String summary;

    private String content;

    private String category;

    private String categoryId;

    private String userId;

    private String status;

    private Integer readTime;

    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<String> tags;
}
