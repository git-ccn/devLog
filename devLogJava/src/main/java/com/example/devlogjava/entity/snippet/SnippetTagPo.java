package com.example.devlogjava.entity.snippet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SnippetTagPo {
    private String snippetId;

    private List<String> tagIds;

    private LocalDateTime createdAt;
}
