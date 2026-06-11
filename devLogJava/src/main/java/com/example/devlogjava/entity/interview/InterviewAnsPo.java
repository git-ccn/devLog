package com.example.devlogjava.entity.interview;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewAnsPo {
    private String id;
    private String interviewId;
    private String answer;
    private Double accuracy;
    private String solution;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
