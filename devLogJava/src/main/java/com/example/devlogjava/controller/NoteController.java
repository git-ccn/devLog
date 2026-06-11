package com.example.devlogjava.controller;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.note.NoteOptionPo;
import com.example.devlogjava.entity.note.NotePo;
import com.example.devlogjava.entity.note.NoteQueryPo;
import com.example.devlogjava.entity.note.NoteStatsPo;
import com.example.devlogjava.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {
    private static final Logger log = LoggerFactory.getLogger(NoteController.class);

    @Autowired
    private NoteService noteService;

    @PostMapping("/addNote")
    public Result<Integer> addNote(@RequestBody NotePo note) {
        try {
            return noteService.addNote(note);
        } catch (Exception e) {
            log.error("新增技术笔记失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updateNote")
    public Result<Integer> updateNote(@RequestBody NotePo note) {
        try {
            return noteService.updateNote(note);
        } catch (Exception e) {
            log.error("编辑技术笔记失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/deleteNote")
    public Result<Integer> deleteNote(@RequestBody NotePo note) {
        try {
            return noteService.deleteNote(note == null ? null : note.getId());
        } catch (Exception e) {
            log.error("删除技术笔记失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/getNote")
    public Result<List<NotePo>> getNote(@RequestBody(required = false) NoteQueryPo query) {
        try {
            return noteService.queryNotes(query);
        } catch (Exception e) {
            log.error("查询技术笔记失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getTag")
    public Result<List<NoteOptionPo>> getTag(@RequestParam(required = false) String name) {
        try {
            return noteService.queryTags(name);
        } catch (Exception e) {
            log.error("查询笔记标签失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getCategory")
    public Result<List<NoteOptionPo>> getCategory() {
        try {
            return noteService.queryCategories();
        } catch (Exception e) {
            log.error("查询笔记分类失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getStats")
    public Result<NoteStatsPo> getStats() {
        try {
            return noteService.queryStats();
        } catch (Exception e) {
            log.error("查询笔记统计失败", e);
            return Result.error(e.getMessage());
        }
    }
}
