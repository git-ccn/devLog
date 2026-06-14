package com.example.devlogjava.service.impl;

import com.example.devlogjava.common.IdUtils;
import com.example.devlogjava.common.Result;
import com.example.devlogjava.common.SecurityUtils;
import com.example.devlogjava.entity.todo.TodoPo;
import com.example.devlogjava.mapper.TodoMapper;
import com.example.devlogjava.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    private TodoMapper todoMapper;

    @Override
    public Result<List<TodoPo>> queryMyTodos() {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return Result.success(todoMapper.queryByUserId(userId));
    }

    @Override
    @Transactional
    public Result<Integer> addTodo(TodoPo todo) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        todo.setId(IdUtils.defaultId(todo.getId(), "TODO"));
        todo.setUserId(userId);
        if (todo.getStatus() == null || todo.getStatus().isBlank()) {
            todo.setStatus("pending");
        }
        int rows = todoMapper.insert(todo);
        return Result.success(rows);
    }

    @Override
    @Transactional
    public Result<Integer> updateTodo(TodoPo todo) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        todo.setUserId(userId);
        int rows = todoMapper.update(todo);
        return Result.success(rows);
    }

    @Override
    @Transactional
    public Result<Integer> deleteTodo(String id) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        int rows = todoMapper.deleteById(id);
        return Result.success(rows);
    }
}
