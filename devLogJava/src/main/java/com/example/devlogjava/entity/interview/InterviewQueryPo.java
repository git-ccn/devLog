package com.example.devlogjava.entity.interview;

import lombok.Data;

@Data
public class InterviewQueryPo {
    private String keyword;
    private String categoryId;
    private String status;
    private String difficulty;
}
