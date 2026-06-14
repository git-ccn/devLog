package com.example.devlogjava.controller;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.todo.TodoPo;
import com.example.devlogjava.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/todo")
public class TodoController {

    private static final Logger log = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    private TodoService todoService;

    @GetMapping("/getTodos")
    public Result<List<TodoPo>> getTodos() {
        try {
            return todoService.queryMyTodos();
        } catch (Exception e) {
            log.error("查询待办事项失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/addTodo")
    public Result<Integer> addTodo(@RequestBody TodoPo todo) {
        try {
            return todoService.addTodo(todo);
        } catch (Exception e) {
            log.error("新增待办事项失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updateTodo")
    public Result<Integer> updateTodo(@RequestBody TodoPo todo) {
        try {
            return todoService.updateTodo(todo);
        } catch (Exception e) {
            log.error("更新待办事项失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/deleteTodo")
    public Result<Integer> deleteTodo(@RequestBody TodoPo todo) {
        try {
            return todoService.deleteTodo(todo == null ? null : todo.getId());
        } catch (Exception e) {
            log.error("删除待办事项失败", e);
            return Result.error(e.getMessage());
        }
    }
}
