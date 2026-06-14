package com.example.devlogjava.controller;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.note.NoteStatsPo;
import com.example.devlogjava.mapper.InterviewMapper;
import com.example.devlogjava.mapper.NoteMapper;
import com.example.devlogjava.mapper.SnippetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private SnippetMapper snippetMapper;

    @GetMapping("/getStats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 1. 掌握状态
            List<Map<String, Object>> statusCounts = interviewMapper.countByStatus();
            long todo = 0, learning = 0, mastered = 0;
            for (Map<String, Object> row : statusCounts) {
                String status = String.valueOf(row.get("status"));
                long count = ((Number) row.get("count")).longValue();
                switch (status) {
                    case "todo": todo = count; break;
                    case "learning": learning = count; break;
                    case "mastered": mastered = count; break;
                }
            }
            Map<String, Long> mastery = new LinkedHashMap<>();
            mastery.put("todo", todo);
            mastery.put("learning", learning);
            mastery.put("mastered", mastered);
            data.put("mastery", mastery);

            // 2. 周活跃数据（近7天）
            String sevenDaysAgo = LocalDate.now().minusDays(6).format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Map<String, Object>> snippetRows = snippetMapper.countByDate(sevenDaysAgo);
            List<Map<String, Object>> noteRows = noteMapper.countByDate(sevenDaysAgo);

            Map<String, Long> snippetDayCount = snippetRows.stream()
                    .collect(Collectors.toMap(
                            r -> String.valueOf(r.get("day")),
                            r -> ((Number) r.get("count")).longValue()
                    ));
            Map<String, Long> noteDayCount = noteRows.stream()
                    .collect(Collectors.toMap(
                            r -> String.valueOf(r.get("day")),
                            r -> ((Number) r.get("count")).longValue()
                    ));

            List<Map<String, Object>> weekly = new ArrayList<>();
            String[] weekLabels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            for (int i = 6; i >= 0; i--) {
                String date = LocalDate.now().minusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("day", weekLabels[6 - i]);
                item.put("snippetCount", snippetDayCount.getOrDefault(date, 0L));
                item.put("noteCount", noteDayCount.getOrDefault(date, 0L));
                weekly.add(item);
            }
            data.put("weekly", weekly);

            // 3. 学习进度
            NoteStatsPo noteStats = noteMapper.queryStats();
            long noteTotal = noteStats != null && noteStats.getTotal() != null ? noteStats.getTotal() : 0;
            long noteDone = noteStats != null && noteStats.getDone() != null ? noteStats.getDone() : 0;
            long totalItems = noteTotal + (todo + learning + mastered);
            long doneItems = noteDone + mastered;
            int progress = totalItems > 0 ? (int) Math.round(doneItems * 100.0 / totalItems) : 0;
            data.put("progress", progress);

            return Result.success(data);
        } catch (Exception e) {
            log.error("查询仪表盘数据失败", e);
            return Result.error(e.getMessage());
        }
    }
}
