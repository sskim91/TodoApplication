package com.study.todo.todo.service;

import com.study.todo.user.domain.User;
import com.study.todo.user.repository.UserRepository;
import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
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
    public TodoResponseDto createTodo(TodoRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + requestDto.getUserId()));
        Todo todo = Todo.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .status(TodoStatus.TODO)
                .user(user)
                .build();
        Todo savedTodo = todoRepository.save(todo);
        return TodoResponseDto.from(savedTodo);
    }

    public List<TodoResponseDto> getAllTodos(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + userId));
        List<Todo> todos = todoRepository.findByUser(user);
        return todos.stream().map(TodoResponseDto::from).collect(Collectors.toList());
    }

    public TodoResponseDto getMostRecentTodo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id: " + userId));
        Todo todo = todoRepository.findTopByUserOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new NoSuchElementException("No todos found for user id: " + userId));
        return TodoResponseDto.from(todo);
    }

    @Transactional
    public TodoResponseDto updateTodoStatus(Long todoId, TodoStatus status) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid todo id: " + todoId));

        if (status == TodoStatus.PENDING && todo.getStatus() != TodoStatus.IN_PROGRESS) {
            throw new IllegalStateException("Can only change to PENDING from IN_PROGRESS");
        }

        todo.changeStatus(status);
        return TodoResponseDto.from(todo);
    }
}
