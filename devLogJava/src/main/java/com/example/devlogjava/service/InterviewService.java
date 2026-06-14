package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.interview.ExamGradeReq;
import com.example.devlogjava.entity.interview.InterviewAnsPo;
import com.example.devlogjava.entity.interview.InterviewQueryPo;

import java.util.List;
import java.util.Map;

public interface InterviewService {
    Result<List<Map<String, Object>>> queryInterviews(InterviewQueryPo query);

    Result<?> addAns(InterviewAnsPo ans);

    Result<?> updateAns(InterviewAnsPo ans);

    Result<List<Map<String, Object>>> queryCategories();

    Result<List<Map<String, Object>>> exam(int count);

    Result<List<Map<String, Object>>> grade(ExamGradeReq req);
}
