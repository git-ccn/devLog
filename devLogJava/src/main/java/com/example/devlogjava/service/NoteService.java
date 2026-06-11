package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.note.NoteOptionPo;
import com.example.devlogjava.entity.note.NotePo;
import com.example.devlogjava.entity.note.NoteQueryPo;
import com.example.devlogjava.entity.note.NoteStatsPo;

import java.util.List;

public interface NoteService {
    Result<Integer> addNote(NotePo note);

    Result<Integer> updateNote(NotePo note);

    Result<Integer> deleteNote(String id);

    Result<List<NotePo>> queryNotes(NoteQueryPo query);

    Result<List<NoteOptionPo>> queryTags(String name);

    Result<List<NoteOptionPo>> queryCategories();

    Result<NoteStatsPo> queryStats();
}
