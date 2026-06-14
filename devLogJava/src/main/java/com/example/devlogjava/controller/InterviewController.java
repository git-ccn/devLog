package com.example.devlogjava.controller;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.interview.ExamGradeReq;
import com.example.devlogjava.entity.interview.InterviewAnsPo;
import com.example.devlogjava.entity.interview.InterviewQueryPo;
import com.example.devlogjava.service.InterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interview")
public class InterviewController {

    private static final Logger log = LoggerFactory.getLogger(InterviewController.class);

    @Autowired
    private InterviewService interviewService;

    @PostMapping("/getInterview")
    public Result<List<Map<String, Object>>> getInterview(@RequestBody(required = false) InterviewQueryPo query) {
        try {
            return interviewService.queryInterviews(query);
        } catch (Exception e) {
            log.error("查询面试题库失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addAns")
    public Result<?> addAns(@RequestBody InterviewAnsPo ans) {
        try {
            return interviewService.addAns(ans);
        } catch (Exception e) {
            log.error("新增作答失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updateAns")
    public Result<?> updateAns(@RequestBody InterviewAnsPo ans) {
        try {
            return interviewService.updateAns(ans);
        } catch (Exception e) {
            log.error("更新作答失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getCategory")
    public Result<List<Map<String, Object>>> getCategory() {
        try {
            return interviewService.queryCategories();
        } catch (Exception e) {
            log.error("查询面试分类失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/exam")
    public Result<List<Map<String, Object>>> exam() {
        try {
            return interviewService.exam(30);
        } catch (Exception e) {
            log.error("生成考试题目失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/grade")
    public Result<List<Map<String, Object>>> grade(@RequestBody ExamGradeReq req) {
        try {
            return interviewService.grade(req);
        } catch (Exception e) {
            log.error("批卷失败", e);
            return Result.error(e.getMessage());
        }
    }
}
