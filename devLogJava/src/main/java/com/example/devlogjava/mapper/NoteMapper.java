package com.example.devlogjava.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.devlogjava.entity.note.NoteOptionPo;
import com.example.devlogjava.entity.note.NoteNoteTagPo;
import com.example.devlogjava.entity.note.NotePo;
import com.example.devlogjava.entity.note.NoteQueryPo;
import com.example.devlogjava.entity.note.NoteStatsPo;
import com.example.devlogjava.entity.note.NoteTagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoteMapper {
    int insertNote(NotePo note);

    int updateNote(NotePo note);

    int deleteNoteById(@Param("id") String id);

    int deleteNoteTagsByNoteId(@Param("noteId") String noteId);

    NoteOptionPo queryCategoryById(@Param("id") String id);

    List<NoteOptionPo> queryTagsByNames(@Param("names") List<String> names);

    int insertTags(@Param("tags") List<NoteOptionPo> tags);

    int insertNoteTags(@Param("rows") List<NoteNoteTagPo> rows);

    List<NotePo> query(@Param("query") NoteQueryPo query);

    IPage<NotePo> queryPage(IPage<NotePo> page, @Param("query") NoteQueryPo query);

    List<NoteTagPo> queryNoteTagsByNoteIds(@Param("noteIds") List<String> noteIds);

    List<NoteOptionPo> queryTags(@Param("name") String name);

    List<NoteOptionPo> queryCategories();

    NoteStatsPo queryStats();

    List<Map<String, Object>> countByDate(@Param("startDate") String startDate);
}
