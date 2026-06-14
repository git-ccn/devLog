package com.example.devlogjava.service;

import com.example.devlogjava.common.Result;
import com.example.devlogjava.entity.todo.TodoPo;

import java.util.List;

public interface TodoService {
    Result<List<TodoPo>> queryMyTodos();

    Result<Integer> addTodo(TodoPo todo);

    Result<Integer> updateTodo(TodoPo todo);

    Result<Integer> deleteTodo(String id);
}
