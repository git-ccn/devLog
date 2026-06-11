package com.example.devlogjava.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.devlogjava.common.IdUtils;
import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.note.NoteNoteTagPo;
import com.example.devlogjava.entity.note.NoteOptionPo;
import com.example.devlogjava.entity.note.NotePo;
import com.example.devlogjava.entity.note.NoteQueryPo;
import com.example.devlogjava.entity.note.NoteStatsPo;
import com.example.devlogjava.entity.note.NoteTagPo;
import com.example.devlogjava.mapper.NoteMapper;
import com.example.devlogjava.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteMapper noteMapper;

    @Override
    @Transactional
    public Result<Integer> addNote(NotePo note) {
        validateAndPrepareForSave(note, true);

        int inserted;
        try {
            inserted = noteMapper.insertNote(note);
        } catch (DuplicateKeyException e) {
            throw buildDuplicateNoteException(e);
        }
        if (inserted <= 0) {
            throw new RuntimeException("新增技术笔记失败");
        }

        saveNoteTags(note);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> updateNote(NotePo note) {
        validateAndPrepareForSave(note, false);

        int updated;
        try {
            updated = noteMapper.updateNote(note);
        } catch (DuplicateKeyException e) {
            throw buildDuplicateNoteException(e);
        }
        if (updated <= 0) {
            throw new RuntimeException("编辑技术笔记失败");
        }

        noteMapper.deleteNoteTagsByNoteId(note.getId());
        saveNoteTags(note);
        return Result.success();
    }

    @Override
    @Transactional
    public Result<Integer> deleteNote(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("笔记ID不能为空");
        }
        String noteId = id.trim();
        noteMapper.deleteNoteTagsByNoteId(noteId);
        int deleted = noteMapper.deleteNoteById(noteId);
        if (deleted <= 0) {
            throw new RuntimeException("笔记不存在");
        }
        return Result.success();
    }

    @Override
    public Result<List<NotePo>> queryNotes(NoteQueryPo query) {
        if (query == null) {
            query = new NoteQueryPo();
        }
        boolean enablePage = query.getPageNum() != null && query.getPageSize() != null;
        List<NotePo> list;
        Page<NotePo> page = null;
        if (enablePage) {
            int pageNum = Math.max(1, query.getPageNum());
            int pageSize = Math.max(1, query.getPageSize());
            page = new Page<>(pageNum, pageSize);
            IPage<NotePo> pageRes = noteMapper.queryPage(page, query);
            list = pageRes == null || pageRes.getRecords() == null ? new ArrayList<>() : pageRes.getRecords();
        } else {
            list = noteMapper.query(query);
        }
        if (list == null) {
            list = new ArrayList<>();
        }

        List<String> noteIds = new ArrayList<>();
        for (NotePo n : list) {
            if (n == null) continue;
            if (n.getId() == null || n.getId().isBlank()) {
                n.setTags(new ArrayList<>());
                continue;
            }
            noteIds.add(n.getId());
            if (n.getTags() == null) {
                n.setTags(new ArrayList<>());
            }
        }

        if (!noteIds.isEmpty()) {
            List<NoteTagPo> rows = noteMapper.queryNoteTagsByNoteIds(noteIds);
            if (rows != null && !rows.isEmpty()) {
                Map<String, List<String>> tagMap = new LinkedHashMap<>();
                for (NoteTagPo r : rows) {
                    if (r == null) continue;
                    if (r.getNoteId() == null || r.getNoteId().isBlank()) continue;
                    if (r.getTagName() == null || r.getTagName().isBlank()) continue;
                    tagMap.computeIfAbsent(r.getNoteId(), (k) -> new ArrayList<>()).add(r.getTagName());
                }
                for (NotePo n : list) {
                    if (n == null) continue;
                    List<String> tags = tagMap.get(n.getId());
                    if (tags == null) continue;
                    n.setTags(tags);
                }
            }
        }

        if (enablePage) {
            page.setRecords(list);
            return Result.successPage(page);
        }
        return Result.success(list);
    }

    @Override
    public Result<List<NoteOptionPo>> queryTags(String name) {
        return Result.success(noteMapper.queryTags(name));
    }

    @Override
    public Result<List<NoteOptionPo>> queryCategories() {
        return Result.success(noteMapper.queryCategories());
    }

    @Override
    public Result<NoteStatsPo> queryStats() {
        return Result.success(noteMapper.queryStats());
    }

    private void fillCategory(NotePo note, String categoryId) {
        NoteOptionPo category = noteMapper.queryCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        note.setCategoryId(category.getId());
        note.setCategory(category.getName());
    }

    private void validateAndPrepareForSave(NotePo note, boolean create) {
        if (note == null) {
            throw new IllegalArgumentException("笔记不能为空");
        }
        if (!create && (note.getId() == null || note.getId().isBlank())) {
            throw new IllegalArgumentException("笔记ID不能为空");
        }
        if (note.getTitle() == null || note.getTitle().isBlank()) {
            throw new IllegalArgumentException("请输入标题");
        }
        String categoryId = resolveIncomingCategoryId(note);
        if (categoryId == null) {
            throw new IllegalArgumentException("请输入分类");
        }
        if (note.getContent() == null || note.getContent().isBlank()) {
            throw new IllegalArgumentException("请输入正文");
        }

        if (create) {
            note.setId(IdUtils.defaultId(note.getId(), "note-"));
            note.setDeleted(note.getDeleted() == null ? 0 : note.getDeleted());
        } else {
            note.setId(note.getId().trim());
        }
        note.setTitle(note.getTitle().trim());
        note.setSummary(note.getSummary() == null ? "" : note.getSummary().trim());
        note.setContent(note.getContent().trim());
        note.setStatus((note.getStatus() == null || note.getStatus().isBlank()) ? "draft" : note.getStatus().trim());
        note.setReadTime(estimateReadTime(note.getContent()));

        fillCategory(note, categoryId);
    }

    private void saveNoteTags(NotePo note) {
        if (note.getTags() == null || note.getTags().isEmpty()) {
            return;
        }

        LinkedHashSet<String> tagNames = new LinkedHashSet<>();
        for (String tag : note.getTags()) {
            if (tag == null || tag.isBlank()) {
                continue;
            }
            tagNames.add(tag.trim());
        }
        if (tagNames.isEmpty()) {
            return;
        }

        List<NoteOptionPo> existingTags = noteMapper.queryTagsByNames(new ArrayList<>(tagNames));
        Map<String, String> tagIdMap = new LinkedHashMap<>();
        Set<String> existingNames = new HashSet<>();
        if (existingTags != null) {
            for (NoteOptionPo tag : existingTags) {
                if (tag == null || tag.getName() == null || tag.getName().isBlank()) {
                    continue;
                }
                existingNames.add(tag.getName());
                if (tag.getId() != null && !tag.getId().isBlank()) {
                    tagIdMap.put(tag.getName(), tag.getId());
                }
            }
        }

        List<NoteOptionPo> newTags = new ArrayList<>();
        for (String name : tagNames) {
            if (existingNames.contains(name)) {
                continue;
            }
            NoteOptionPo tag = new NoteOptionPo();
            tag.setId(IdUtils.defaultId(tag.getId(), "tag-"));
            tag.setName(name);
            newTags.add(tag);
            tagIdMap.put(name, tag.getId());
        }

        if (!newTags.isEmpty()) {
            int inserted = noteMapper.insertTags(newTags);
            if (inserted <= 0) {
                throw new RuntimeException("新增笔记标签失败");
            }
        }

        List<NoteNoteTagPo> rows = new ArrayList<>();
        for (String name : tagNames) {
            String tagId = tagIdMap.get(name);
            if (tagId == null || tagId.isBlank()) {
                continue;
            }
            NoteNoteTagPo row = new NoteNoteTagPo();
            row.setId(IdUtils.defaultId(row.getId(), "notetag-"));
            row.setNoteId(note.getId());
            row.setTagId(tagId);
            rows.add(row);
        }
        if (rows.isEmpty()) {
            return;
        }
        int inserted = noteMapper.insertNoteTags(rows);
        if (inserted <= 0) {
            throw new RuntimeException("新增笔记标签关联失败");
        }
    }

    private String resolveIncomingCategoryId(NotePo note) {
        if (note == null) {
            return null;
        }
        if (note.getCategoryId() != null && !note.getCategoryId().isBlank()) {
            return note.getCategoryId().trim();
        }
        if (note.getCategory() != null && !note.getCategory().isBlank()) {
            return note.getCategory().trim();
        }
        return null;
    }

    private int estimateReadTime(String content) {
        String plainText = stripMarkdown(content);
        return Math.max(1, (int) Math.ceil(plainText.length() / 220.0d));
    }

    private String stripMarkdown(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        return content
                .replaceAll("```[\\s\\S]*?```", " ")
                .replaceAll("`[^`]*`", " ")
                .replaceAll("!\\[[^\\]]*]\\([^)]*\\)", " ")
                .replaceAll("\\[([^\\]]*)]\\([^)]*\\)", "$1")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("[#>*_~\\-]+", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private RuntimeException buildDuplicateNoteException(DuplicateKeyException e) {
        Throwable cause = e.getMostSpecificCause();
        String message = cause != null ? cause.getMessage() : e.getMessage();
        if (message != null && message.contains("uk_note_note_title")) {
            return new RuntimeException("技术笔记名称已存在");
        }
        return new RuntimeException("数据重复，请检查后重试");
    }
}
