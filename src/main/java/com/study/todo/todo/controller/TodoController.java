package com.study.todo.todo.controller;

import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponseDto> createTodo(@PathVariable Long userId, @RequestBody TodoRequestDto todoRequestDto) {
        return ResponseEntity.ok(todoService.createTodo(userId, todoRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<TodoResponseDto>> getAllTodos(@PathVariable Long userId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "createdAt") String sort,
                                                             @RequestParam(defaultValue = "desc") String order) {
        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return ResponseEntity.ok(todoService.getAllTodos(userId, pageable));
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponseDto> getTodo(@PathVariable Long userId, @PathVariable Long todoId) {
        return ResponseEntity.ok(todoService.getTodo(userId, todoId));
    }

    @PutMapping("/{todoId}/status")
    public ResponseEntity<TodoResponseDto> updateTodoStatus(@PathVariable Long userId, @PathVariable Long todoId, @RequestBody TodoRequestDto requestDto) {
        return ResponseEntity.ok(todoService.updateTodoStatus(userId, todoId, requestDto.getStatus()));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long userId, @PathVariable Long todoId) {
        todoService.deleteTodo(userId, todoId);
        return ResponseEntity.noContent().build();
    }
}
