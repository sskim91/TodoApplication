package com.study.todo.todo.service;

import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.repository.TodoRepository;
import com.study.todo.user.domain.User;
import com.study.todo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TodoServiceTest {

    @InjectMocks
    TodoService todoService;

    @Mock
    UserRepository userRepository;

    @Mock
    TodoRepository todoRepository;

    private User user;
    private Todo todo;

    @BeforeEach
    public void setup() {
        // Initialize PasswordEncoder and create User and Todo
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .id(1L)
                .build();

        todo = Todo.builder()
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .user(user)
                .build();


        // Clear repositories
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 사용자의 Todo 항목을 생성할 수 있어야 한다")
    public void testCreateTodo() {
        // Mock repository responses
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Create request DTO
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .build();

        // Call service method
        TodoResponseDto responseDto = todoService.createTodo(1L, requestDto);

        // Validate response
        assertThat(responseDto.getTitle()).isEqualTo("Test Todo");
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("특정 사용자의 모든 Todo 항목을 조회할 수 있어야 한다")
    public void testGetAllTodos() {
        // Mock repository response
        Pageable pageable = PageRequest.of(0, 10);
        when(todoRepository.findByUserId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(todo)));

        // Call service method
        List<TodoResponseDto> todos = todoService.getAllTodos(1L, pageable);

        // Validate response
        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).getTitle()).isEqualTo("Test Todo");
        verify(todoRepository, times(1)).findByUserId(any(Long.class), any(Pageable.class));
    }

    @Test
    @DisplayName("특정 사용자의 특정 Todo 항목을 조회할 수 있어야 한다")
    public void testGetTodo() {
        // Mock repository response
        when(todoRepository.findByIdAndUserId(any(Long.class), any(Long.class))).thenReturn(Optional.of(todo));

        // Call service method
        TodoResponseDto responseDto = todoService.getTodo(1L, 1L);

        // Validate response
        assertThat(responseDto.getTitle()).isEqualTo("Test Todo");
        verify(todoRepository, times(1)).findByIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    @DisplayName("특정 사용자의 가장 최근 Todo 항목을 조회할 수 있어야 한다")
    public void testGetMostRecentTodos() {
        // Mock repository response
        Pageable pageable = PageRequest.of(0, 2);
        when(todoRepository.findByUserId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(todo, todo)));

        // Call service method
        List<TodoResponseDto> responseDtos = todoService.getMostRecentTodo(1L, 2);

        // Validate response
        assertThat(responseDtos).hasSize(2);
        assertThat(responseDtos.get(0).getTitle()).isEqualTo("Test Todo");
        verify(todoRepository, times(1)).findByUserId(any(Long.class), any(Pageable.class));
    }

    @Test
    @DisplayName("특정 사용자의 Todo 상태를 업데이트할 수 있어야 한다")
    public void testUpdateTodoStatus() {
        // Mock repository responses
        when(todoRepository.findByIdAndUserId(any(Long.class), any(Long.class))).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // Create request DTO
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .status(TodoStatus.DONE)
                .build();

        // Call service method
        TodoResponseDto responseDto = todoService.updateTodoStatus(1L, 1L, requestDto.getStatus());

        // Validate response
        assertThat(responseDto.getStatus()).isEqualTo(TodoStatus.DONE);
        verify(todoRepository, times(1)).findByIdAndUserId(any(Long.class), any(Long.class));
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("특정 사용자의 Todo 항목을 삭제할 수 있어야 한다")
    public void testDeleteTodo() {
        // Mock repository response
        when(todoRepository.findByIdAndUserId(any(Long.class), any(Long.class))).thenReturn(Optional.of(todo));
        doNothing().when(todoRepository).delete(any(Todo.class));

        // Call service method
        todoService.deleteTodo(1L, 1L);

        // Validate interaction with repository
        verify(todoRepository, times(1)).findByIdAndUserId(any(Long.class), any(Long.class));
        verify(todoRepository, times(1)).delete(any(Todo.class));
    }
}
