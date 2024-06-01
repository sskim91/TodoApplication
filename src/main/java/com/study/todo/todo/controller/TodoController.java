package com.study.todo.todo.controller;

import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(@RequestBody TodoRequestDto todoRequestDto) {
        return ResponseEntity.ok(todoService.createTodo(todoRequestDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<TodoResponseDto>> getAllTodos(@PathVariable Long userId) {
        return ResponseEntity.ok(todoService.getAllTodos(userId));
    }

    @GetMapping("/recent/{userId}")
    public ResponseEntity<TodoResponseDto> getMostRecentTodo(@PathVariable Long userId) {
        return ResponseEntity.ok(todoService.getMostRecentTodo(userId));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponseDto> updateTodoStatus(@PathVariable Long todoId, @RequestParam TodoStatus status) {
        return ResponseEntity.ok(todoService.updateTodoStatus(todoId, status));
    }
}
