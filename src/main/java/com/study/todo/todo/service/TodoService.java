package com.study.todo.todo.service;

import com.study.todo.user.domain.User;
import com.study.todo.user.repository.UserRepository;
import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public TodoResponseDto createTodo(Long userId, TodoRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Todo todo = Todo.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .status(requestDto.getStatus())
                .user(user)
                .build();
        todoRepository.save(todo);
        return TodoResponseDto.from(todo);
    }

    public List<TodoResponseDto> getAllTodos(Long userId, Pageable pageable) {
        final Page<Todo> todos = todoRepository.findByUserId(userId, pageable);
        return todos.stream()
                .map(TodoResponseDto::from)
                .toList();
    }

    public TodoResponseDto getTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo ID or user ID"));
        return TodoResponseDto.from(todo);
    }

    public List<TodoResponseDto> getMostRecentTodo(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<Todo> todos = todoRepository.findByUserId(userId, pageable);

        return todos.stream()
                .map(TodoResponseDto::from)
                .toList();
    }

    @Transactional
    public TodoResponseDto updateTodoStatus(Long userId, Long todoId, TodoStatus status) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo ID or user ID"));
        todo.updateStatus(status);
        todoRepository.save(todo);
        return TodoResponseDto.from(todo);
    }

    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo ID or user ID"));
        todoRepository.delete(todo);
    }
}
