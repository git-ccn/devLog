package com.example.devlogjava.entity.snippet;

import lombok.Data;

import java.util.List;

@Data
public class SnippetQueryPo {
    private String title;
    private String content;
    private String keyword;
    private String language;
    private boolean showDeleted = false;
    private List<String> tagNames;
    private Integer pageNum;
    private Integer pageSize;
}
