package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.snippet.SnippetPo;
import com.example.devlogjava.entity.snippet.SnippetQueryPo;
import com.example.devlogjava.entity.snippet.TagPo;

import java.util.List;

public interface SnippetService {
    Result<List<SnippetPo>> querySnippets(SnippetQueryPo query);

    Result<List<TagPo>> queryTags(String name);

    Result<List<String>> queryLanguages();

    Result<Integer> insertSnippet(SnippetPo snippet);

    Result<Integer> updateSnippet(SnippetPo snippet);

    Result<Integer> deleteSnippetById(String id);

    Result<Integer> hardDeleteSnippetById(String id);

    Result<Integer> deleteTagById(String id);

}
