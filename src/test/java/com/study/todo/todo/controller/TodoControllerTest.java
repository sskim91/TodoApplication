package com.study.todo.todo.controller;

import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.service.TodoService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.study.todo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Todo todo;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .build();

        todo = Todo.builder()
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Todo 생성 테스트")
    @WithMockUser(username = "testuser")
    public void testCreateTodo() throws Exception {
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .build();

        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.createTodo(any(Long.class), any(TodoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/users/{userId}/todos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("This is a test todo"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("모든 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetAllTodos() throws Exception {
        TodoResponseDto responseDto1 = TodoResponseDto.builder()
                .id(1L)
                .title("Test Todo 1")
                .description("This is test todo 1")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TodoResponseDto responseDto2 = TodoResponseDto.builder()
                .id(2L)
                .title("Test Todo 2")
                .description("This is test todo 2")
                .status(TodoStatus.DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TodoResponseDto> responseDtos = Arrays.asList(responseDto1, responseDto2);

        when(todoService.getAllTodos(any(Long.class), any(Pageable.class)))
                .thenReturn(responseDtos);

        mockMvc.perform(get("/api/v1/users/{userId}/todos", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Todo 1"))
                .andExpect(jsonPath("$[1].title").value("Test Todo 2"));
    }

    @Test
    @DisplayName("특정 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetTodo() throws Exception {
        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.getTodo(any(Long.class), any(Long.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{userId}/todos/{todoId}", 1L, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("This is a test todo"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("가장 최근 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetMostRecentTodo() throws Exception {
        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("Most Recent Todo")
                .description("This is the most recent todo")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.getMostRecentTodo(any(Long.class), any(Integer.class))).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/users/{userId}/todos/recent", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Most Recent Todo"))
                .andExpect(jsonPath("$.description").value("This is the most recent todo"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    @DisplayName("Todo 상태 업데이트 테스트")
    @WithMockUser(username = "testuser")
    public void testUpdateTodoStatus() throws Exception {
        TodoRequestDto requestDto = TodoRequestDto.builder()
                .status(TodoStatus.DONE)
                .build();

        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.updateTodoStatus(any(Long.class), any(Long.class), any(TodoStatus.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/users/{userId}/todos/{todoId}/status", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    @WithMockUser(username = "testuser")
    public void testDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}/todos/{todoId}", 1L, 1L))
                .andExpect(status().isNoContent());
    }
}
