package com.example.devlogjava.service.impl;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.interview.InterviewAnsPo;
import com.example.devlogjava.entity.interview.InterviewPo;
import com.example.devlogjava.entity.interview.InterviewQueryPo;
import com.example.devlogjava.entity.note.NoteOptionPo;
import com.example.devlogjava.mapper.InterviewMapper;
import com.example.devlogjava.mapper.NoteMapper;
import com.example.devlogjava.service.InterviewService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InterviewServiceImpl implements InterviewService {

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired(required = false)
    private NoteMapper noteMapper;

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String EVAL_PROMPT = """
            你是一位资深面试官。请根据以下面试题目和用户的作答，完成两件事：
            1. 评估用户回答的正确率，给出0-100的分数。
            2. 用Markdown格式生成以下两部分内容：
               - **答案分析**：对用户作答的点评，指出正确之处和不足之处
               - **参考答案**：少于500字的权威参考答案，条理清晰
            
            请严格按照以下JSON格式返回（不要包含任何其他内容）：
            {"accuracy": 数字, "solution": "## 答案分析\\n...\\n\\n## 参考答案\\n..."}
            
            题目：%s
            
            用户作答：%s
            """;

    @Override
    public Result<List<Map<String, Object>>> queryInterviews(InterviewQueryPo query) {
        if (query == null) {
            query = new InterviewQueryPo();
        }
        List<InterviewPo> list = interviewMapper.query(query);
        if (list == null) {
            list = Collections.emptyList();
        }

        List<Map<String, Object>> result = list.stream().map(po -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", po.getId());
            map.put("title", po.getTitle());
            map.put("category", po.getCategoryName());
            map.put("categoryId", po.getCategoryId());
            map.put("status", po.getStatus());
            map.put("difficulty", po.getDifficulty());
            map.put("question", po.getQuestion());
            map.put("myAnswer", po.getMyAnswer());
            map.put("accuracy", po.getAccuracy());
            map.put("solution", po.getSolution());
            map.put("createdAt", po.getCreatedAt() != null ? po.getCreatedAt().toString() : null);
            map.put("updatedAt", po.getUpdatedAt() != null ? po.getUpdatedAt().toString() : null);
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> addAns(InterviewAnsPo ans) {
        ans.setId(UUID.randomUUID().toString().replace("-", ""));
        evaluateAndFill(ans);
        interviewMapper.insertAns(ans);
        updateStatusByAccuracy(ans.getInterviewId(), ans.getAccuracy());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ans.getId());
        result.put("accuracy", ans.getAccuracy());
        result.put("solution", ans.getSolution());
        return Result.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> updateAns(InterviewAnsPo ans) {
        evaluateAndFill(ans);
        interviewMapper.updateAnsByInterviewId(ans.getInterviewId(), ans.getAnswer(), ans.getAccuracy(), ans.getSolution());
        updateStatusByAccuracy(ans.getInterviewId(), ans.getAccuracy());
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", ans.getInterviewId());
        result.put("accuracy", ans.getAccuracy());
        result.put("solution", ans.getSolution());
        return Result.success(result);
    }

    @Override
    public Result<List<Map<String, Object>>> queryCategories() {
        if (noteMapper == null) {
            return Result.success(Collections.emptyList());
        }
        List<NoteOptionPo> list = noteMapper.queryCategories();
        if (list == null) {
            list = Collections.emptyList();
        }
        List<Map<String, Object>> result = list.stream().map(po -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", po.getId());
            map.put("name", po.getName());
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 调用 DeepSeek 评估用户作答，生成正确率和参考答案
     */
    private void evaluateAndFill(InterviewAnsPo ans) {
        if (ans.getAnswer() == null || ans.getAnswer().isBlank()) {
            ans.setAccuracy(null);
            ans.setSolution(null);
            return;
        }

        try {
            String question = interviewMapper.queryQuestionById(ans.getInterviewId());
            if (question == null || question.isBlank()) {
                log.warn("未找到题目 interviewId={}", ans.getInterviewId());
                return;
            }

            String prompt = String.format(EVAL_PROMPT, question, ans.getAnswer());
            String aiResponse = chatLanguageModel.chat(prompt);

            String json = extractJson(aiResponse);
            JsonNode node = objectMapper.readTree(json);
            if (node.has("accuracy")) {
                ans.setAccuracy(node.get("accuracy").asDouble());
            }
            if (node.has("solution")) {
                ans.setSolution(node.get("solution").asText());
            }
            log.info("AI评估完成 interviewId={} accuracy={}", ans.getInterviewId(), ans.getAccuracy());
        } catch (Exception e) {
            log.error("AI评估失败 interviewId={}", ans.getInterviewId(), e);
        }
    }

    /**
     * 根据正确率自动更新面试题状态：accuracy >= 80 → mastered，否则 → learning
     */
    private void updateStatusByAccuracy(String interviewId, Double accuracy) {
        if (accuracy == null) return;
        String status = accuracy >= 80 ? "mastered" : "learning";
        interviewMapper.updateStatusByInterviewId(interviewId, status);
        log.info("自动更新状态 interviewId={} status={}", interviewId, status);
    }

    private String extractJson(String aiResponse) {
        String json = aiResponse.trim();
        if (json.startsWith("```json")) {
            json = json.substring(7);
        }
        if (json.startsWith("```")) {
            json = json.substring(3);
        }
        if (json.endsWith("```")) {
            json = json.substring(0, json.length() - 3);
        }
        return json.trim();
    }
}
