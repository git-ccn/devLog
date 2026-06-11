package com.example.devlogjava.entity.interview;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewPo {
    private String id;
    private String title;
    private String categoryId;
    private String status;
    private String difficulty;
    private String question;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** 关联查询 note_category.name */
    private String categoryName;

    /** 关联查询 interview_ans.answer */
    private String myAnswer;

    /** 关联查询 interview_ans.accuracy */
    private Double accuracy;

    /** 关联查询 interview_ans.solution */
    private String solution;
}
