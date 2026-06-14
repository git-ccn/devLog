package com.example.devlogjava.mapper;

import com.example.devlogjava.entity.todo.TodoPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TodoMapper {
    List<TodoPo> queryByUserId(@Param("userId") String userId);

    int insert(TodoPo todo);

    int update(TodoPo todo);

    int deleteById(@Param("id") String id);
}
