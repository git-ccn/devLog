package com.example.devlogjava.entity.note;

import lombok.Data;

import java.util.List;

@Data
public class NoteQueryPo {
    private String keyword;

    private String categoryId;

    private String userId;

    private String status;

    private List<String> tagIds;

    private Integer pageNum;

    private Integer pageSize;
}
