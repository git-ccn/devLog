package com.example.devlogjava.controller;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.snippet.SnippetPo;
import com.example.devlogjava.entity.snippet.SnippetQueryPo;
import com.example.devlogjava.entity.snippet.TagPo;
import com.example.devlogjava.service.SnippetService;
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
@RequestMapping("/snippet")
public class SnippetController {

    private static final Logger log = LoggerFactory.getLogger(SnippetController.class);

    @Autowired
    private SnippetService snippetService;

    @PostMapping("/getSnippet")
    public Result<List<SnippetPo>> getSnippet(@RequestBody(required = false) SnippetQueryPo query) {
        try {
            return snippetService.querySnippets(query);
        } catch (Exception e) {
            log.error("查询代码片段失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addSnippet")
    public Result<Integer> addSnippet(@RequestBody SnippetPo snippet) {
        try {
            return snippetService.insertSnippet(snippet);
        } catch (Exception e) {
            log.error("新增代码片段失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updateSnippet")
    public Result<Integer> updateSnippet(@RequestBody SnippetPo snippet) {
        try {
            return snippetService.updateSnippet(snippet);
        } catch (Exception e) {
            log.error("更新代码片段失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getTags")
    public Result<List<TagPo>> getTags(@RequestParam(required = false) String name) {
        try {
            return snippetService.queryTags(name);
        } catch (Exception e) {
            log.error("查询标签失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/getLanguages")
    public Result<List<String>> getLanguages() {
        try {
            return snippetService.queryLanguages();
        } catch (Exception e) {
            log.error("查询语言失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/deleteSnippet")
    public Result<Integer> deleteSnippet(@RequestBody SnippetPo snippet) {
        try {
            return snippetService.deleteSnippetById(snippet.getId());
        } catch (Exception e) {
            log.error("删除代码片段失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/hardDeleteSnippet")
    public Result<Integer> hardDeleteSnippet(@RequestBody SnippetPo snippet) {
        try {
            return snippetService.hardDeleteSnippetById(snippet.getId());
        } catch (Exception e) {
            log.error("硬删除代码片段失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/deleteTag")
    public Result<Integer> deleteTag(@RequestBody TagPo tag) {
        try {
            return snippetService.deleteTagById(tag.getId());
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error(e.getMessage());
        }
    }
}
