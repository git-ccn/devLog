package com.example.devlogjava.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.devlogjava.entity.snippet.SnippetPo;
import com.example.devlogjava.entity.snippet.SnippetQueryPo;
import com.example.devlogjava.entity.snippet.SnippetTagPo;
import com.example.devlogjava.entity.snippet.TagPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SnippetMapper {
    List<SnippetPo> query(@Param("query") SnippetQueryPo query);

    IPage<SnippetPo> queryPage(IPage<SnippetPo> page, @Param("query") SnippetQueryPo query);

    int insert(SnippetPo snippet);

    int updateSnippet(SnippetPo snippet);

    int insertSnippetTag(SnippetTagPo snippetTag);

    int updateTag(TagPo tag);

    boolean isTagNameSameById(@Param("id") String id, @Param("name") String name);

    int softDeleteById(@Param("id") String id);

    int deleteSnippetTagBySnippetId(@Param("snippetId") String snippetId);

    int deleteSnippetTagByTagId(@Param("tagId") String tagId);

    int deleteSnippetById(@Param("id") String id);

    int deleteTagById(@Param("id") String id);

    List<String> querySnippetIdsWithOnlyThisTag(@Param("tagId") String tagId);

    /**
     * 根据标签名称查询不存在的标签名称
     * @param names 标签名称列表
     * @return 不存在的标签名称列表
     */
    List<String> queryNotExistTagNames(@Param("names") List<String> names);

    List<TagPo> queryTagPosByNames(@Param("names") List<String> names);

    /**
     * 批量插入标签
     * @param tags 标签列表
     * @return 插入数量
     */
    int insertTags(List<TagPo> tags);

    List<TagPo> queryTags(@Param("name") String name);

    List<String> queryLanguages();

    List<SnippetTagPo> querySnippetTagsBySnippetIds(@Param("snippetIds") List<String> snippetIds);

    List<TagPo> queryTagsBySnippetId(@Param("snippetId") String snippetId);

    List<Map<String, Object>> countByDate(@Param("startDate") String startDate);
}
