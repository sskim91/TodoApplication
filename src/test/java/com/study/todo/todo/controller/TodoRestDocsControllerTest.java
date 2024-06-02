package com.study.todo.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.service.TodoService;
import com.study.todo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class, MockitoExtension.class})
@WebMvcTest(TodoController.class)
public class TodoRestDocsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Todo todo;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .defaultRequest(patch("/**").with(csrf()))
                .defaultRequest(put("/**").with(csrf()))
                .defaultRequest(delete("/**").with(csrf()))
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();

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
                .title("테스트 투두")
                .description("테스트 투두 내용")
                .status(TodoStatus.TODO)
                .build();

        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("테스스 투두")
                .description("테스트 투두 내용")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.createTodo(any(Long.class), any(TodoRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/users/{userId}/todos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("create-todo",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("할 일의 제목"),
                                fieldWithPath("description").description("할 일의 설명"),
                                fieldWithPath("status").description("할 일의 상태")
                        ),
                        responseFields(
                                fieldWithPath("id").description("할 일의 ID"),
                                fieldWithPath("title").description("할 일의 제목"),
                                fieldWithPath("description").description("할 일의 설명"),
                                fieldWithPath("status").description("할 일의 상태"),
                                fieldWithPath("createdAt").description("할 일의 생성 시간"),
                                fieldWithPath("updatedAt").description("할 일의 마지막 업데이트 시간")
                        )
                ));
    }

    @Test
    @DisplayName("모든 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetAllTodos() throws Exception {
        TodoResponseDto responseDto1 = TodoResponseDto.builder()
                .id(1L)
                .title("투두1 제목")
                .description("내용1")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TodoResponseDto responseDto2 = TodoResponseDto.builder()
                .id(2L)
                .title("투두2 제목")
                .description("내용2")
                .status(TodoStatus.DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TodoResponseDto> responseDtos = Arrays.asList(responseDto1, responseDto2);

        when(todoService.getAllTodos(any(Long.class))).thenReturn(responseDtos);

        mockMvc.perform(get("/api/v1/users/{userId}/todos", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-all-todos",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("할 일의 ID"),
                                fieldWithPath("[].title").description("할 일의 제목"),
                                fieldWithPath("[].description").description("할 일의 설명"),
                                fieldWithPath("[].status").description("할 일의 상태"),
                                fieldWithPath("[].createdAt").description("할 일의 생성 시간"),
                                fieldWithPath("[].updatedAt").description("할 일의 마지막 업데이트 시간")
                        )
                ));
    }

    @Test
    @DisplayName("특정 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetTodo() throws Exception {
        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("투두 제목")
                .description("내용")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.getTodo(any(Long.class), any(Long.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{userId}/todos/{todoId}", 1L, 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-todo",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID"),
                                parameterWithName("todoId").description("할 일의 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("할 일의 ID"),
                                fieldWithPath("title").description("할 일의 제목"),
                                fieldWithPath("description").description("할 일의 설명"),
                                fieldWithPath("status").description("할 일의 상태"),
                                fieldWithPath("createdAt").description("할 일의 생성 시간"),
                                fieldWithPath("updatedAt").description("할 일의 마지막 업데이트 시간")
                        )
                ));
    }

    @Test
    @DisplayName("가장 최근 Todo 조회 테스트")
    @WithMockUser(username = "testuser")
    public void testGetMostRecentTodo() throws Exception {
        TodoResponseDto responseDto = TodoResponseDto.builder()
                .id(1L)
                .title("투두 제목")
                .description("내용")
                .status(TodoStatus.TODO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.getMostRecentTodo(any(Long.class))).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{userId}/todos/recent", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-most-recent-todo",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("할 일의 ID"),
                                fieldWithPath("title").description("할 일의 제목"),
                                fieldWithPath("description").description("할 일의 설명"),
                                fieldWithPath("status").description("할 일의 상태"),
                                fieldWithPath("createdAt").description("할 일의 생성 시간"),
                                fieldWithPath("updatedAt").description("할 일의 마지막 업데이트 시간")
                        )
                ));
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
                .title("투두 제목")
                .description("내용")
                .status(TodoStatus.DONE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.updateTodoStatus(any(Long.class), any(Long.class), any(TodoStatus.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/users/{userId}/todos/{todoId}/status", 1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("update-todo-status",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID"),
                                parameterWithName("todoId").description("할 일의 ID")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("description").description("내용"),
                                fieldWithPath("status").description("할 일의 새로운 상태")
                        ),
                        responseFields(
                                fieldWithPath("id").description("할 일의 ID"),
                                fieldWithPath("title").description("할 일의 제목"),
                                fieldWithPath("description").description("할 일의 설명"),
                                fieldWithPath("status").description("할 일의 상태"),
                                fieldWithPath("createdAt").description("할 일의 생성 시간"),
                                fieldWithPath("updatedAt").description("할 일의 마지막 업데이트 시간")
                        )
                ));
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    @WithMockUser(username = "testuser")
    public void testDeleteTodo() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}/todos/{todoId}", 1L, 1L))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("delete-todo",
                        pathParameters(
                                parameterWithName("userId").description("사용자의 ID"),
                                parameterWithName("todoId").description("할 일의 ID")
                        )
                ));
    }
}

