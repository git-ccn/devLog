package com.example.devlogjava.entity.snippet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SnippetPo {
    private String id;

    private String title;

    private String content;

    private String language;

    private String userId;

    private Integer deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    private List<TagPo> tags;
}
