package com.example.devlogjava.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.snippet.SnippetQueryPo;
import com.example.devlogjava.entity.snippet.SnippetPo;
import com.example.devlogjava.entity.snippet.SnippetTagPo;
import com.example.devlogjava.entity.snippet.TagPo;
import com.example.devlogjava.mapper.SnippetMapper;
import com.example.devlogjava.service.SnippetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SnippetServiceImpl implements SnippetService {
    @Autowired
    private SnippetMapper snippetMapper;

    @Override
    public Result<List<SnippetPo>> querySnippets(SnippetQueryPo query) {
        if (query == null) {
            query = new SnippetQueryPo();
        }
        boolean enablePage = query.getPageNum() != null && query.getPageSize() != null;
        List<SnippetPo> list;
        Page<SnippetPo> page = null;
        if (enablePage) {
            int pageNum = Math.max(1, query.getPageNum());
            int pageSize = Math.max(1, query.getPageSize());
            page = new Page<>(pageNum, pageSize);
            IPage<SnippetPo> pageRes = snippetMapper.queryPage(page, query);
            list = pageRes == null || pageRes.getRecords() == null ? new ArrayList<>() : pageRes.getRecords();
        } else {
            list = snippetMapper.query(query);
        }
        if (list == null) {
            list = new ArrayList<>();
        }

       for (SnippetPo s : list) {
            if (s == null) continue;
            if (s.getId() == null || s.getId().isBlank()) {
                s.setTags(new ArrayList<>());
                continue;
            }
            List<TagPo> tags = snippetMapper.queryTagsBySnippetId(s.getId());
            if (tags == null || tags.isEmpty()) {
                s.setTags(new ArrayList<>());
                continue;
            }
            LinkedHashMap<String, TagPo> unique = new LinkedHashMap<>();
            for (TagPo t : tags) {
                if (t == null) continue;
                if (t.getId() == null || t.getId().isBlank()) continue;
                if (t.getName() == null || t.getName().isBlank()) continue;
                unique.putIfAbsent(t.getId(), t);
            }
            s.setTags(new ArrayList<>(unique.values()));
        }

        if (enablePage) {
            page.setRecords(list);
            return Result.successPage(page);
        }
        return Result.success(list);
    }

    @Override
    public Result<List<TagPo>> queryTags(String name) {
        return Result.success(snippetMapper.queryTags(name));
    }

    @Override
    public Result<List<String>> queryLanguages() {
        return Result.success(snippetMapper.queryLanguages());
    }

    @Override
    public Result<Integer> deleteSnippetById(String id) {
        int deleted = snippetMapper.softDeleteById(id);
        if (deleted <= 0) throw new RuntimeException("删除代码片段失败");
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> hardDeleteSnippetById(String id) {
        snippetMapper.deleteSnippetTagBySnippetId(id);
        int deleted = snippetMapper.deleteSnippetById(id);
        if (deleted <= 0) throw new RuntimeException("硬删除代码片段失败");
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> insertSnippet(SnippetPo snippet) {
        if (snippet.getId() == null || snippet.getId().isBlank()) {
            snippet.setId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (snippet.getDeleted() == null) {
            snippet.setDeleted(0);
        }
        int inserted;
        try {
            inserted = snippetMapper.insert(snippet);
        } catch (DuplicateKeyException e) {
            throw buildDuplicateSnippetException(e);
        }
        if (inserted <= 0) throw new RuntimeException("新增代码片段失败");

        boolean success = saveSnippetTagAndTag(snippet,"insert");
        if (!success) {
            log.error("新增代码片段关联标签失败");
            throw new RuntimeException("新增代码片段关联标签失败"); 
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> updateSnippet(SnippetPo snippet) {
        if (snippet.getId() == null || snippet.getId().isBlank()) {
            throw new IllegalArgumentException("更新代码片段时必须提供有效的ID");
        }
        int updated;
        try {
            updated = snippetMapper.updateSnippet(snippet);
        } catch (DuplicateKeyException e) {
            throw buildDuplicateSnippetException(e);
        }
        if (updated <= 0) throw new RuntimeException("更新代码片段失败");

        if (snippet.getTags() == null) return Result.success();

        boolean success = saveSnippetTagAndTag(snippet,"update");
        if (!success) {
            log.error("更新代码片段关联标签失败");
            throw new RuntimeException("更新代码片段关联标签失败");
        }

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> deleteTagById(String id) {
        List<String> blockedSnippetIds = snippetMapper.querySnippetIdsWithOnlyThisTag(id);
        if (blockedSnippetIds != null && !blockedSnippetIds.isEmpty()) {
            throw new RuntimeException("该标签是部分代码片段的唯一标签，无法删除");
        }
        snippetMapper.deleteSnippetTagByTagId(id);
        int deleted = snippetMapper.deleteTagById(id);
        if (deleted <= 0) throw new RuntimeException("删除标签失败");
        return Result.success();
    }

    
    /**
     * 保存代码片段关联的标签信息，统一处理新增标签、片段与标签的关联关系维护
     * 先处理标签列表中无ID的新标签，生成唯一ID并插入标签表；再根据操作类型处理关联关系：
     * 更新操作会先删除该片段原有所有标签关联，新增操作直接创建关联，最终将当前片段与所有关联标签ID重新建立关联写入片段标签关联表
     *
     * @param snippet 包含标签列表的代码片段实体
     * @param type 操作类型，支持"insert"(新增片段)、"update"(更新片段)两种取值
     * @return 操作是否成功，成功返回true，失败返回false
     */
    public boolean saveSnippetTagAndTag(SnippetPo snippet,String type){
        if (snippet.getTags() != null && !snippet.getTags().isEmpty()) {
            List<TagPo> tagPos = new ArrayList<>();
            for(TagPo t : snippet.getTags()){
                String id = t.getId();
                if (id == null || id.isBlank()) {
                    id = UUID.randomUUID().toString().replace("-", "");
                    t.setId(id);
                    tagPos.add(t);
                }
            }
            if (!tagPos.isEmpty()) {
                int insertedTag = snippetMapper.insertTags(tagPos);
                if (insertedTag <= 0) return false;
            }

            if (type.equals("update")) {
                snippetMapper.deleteSnippetTagBySnippetId(snippet.getId());
            }
            SnippetTagPo snippetTagPo = new SnippetTagPo();
            snippetTagPo.setSnippetId(snippet.getId());
            snippetTagPo.setTagIds(snippet.getTags().stream().map(TagPo::getId).toList());
            int insertedSnippetTag = snippetMapper.insertSnippetTag(snippetTagPo);
            if (insertedSnippetTag <= 0) return false; 
        }
        return true; 
    }

    private RuntimeException buildDuplicateSnippetException(DuplicateKeyException e) {
        Throwable cause = e.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : e.getMessage();
        if (message != null && message.contains("uk_devlog_snippet_title")) {
            return new RuntimeException("代码片段名称已存在");
        }
        return new RuntimeException("数据重复，请检查后重试");
    }
}
