package com.example.devlogjava.entity.interview;

import lombok.Data;
import java.util.List;

@Data
public class ExamGradeReq {
    private List<ExamItem> items;

    @Data
    public static class ExamItem {
        private String id;
        private String title;
        private String question;
        private String answer;
    }
}
