package com.study.todo.todo.service;

import com.study.todo.user.domain.User;
import com.study.todo.user.dto.UserRequestDto;
import com.study.todo.user.dto.UserResponseDto;
import com.study.todo.user.repository.UserRepository;
import com.study.todo.user.service.UserService;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.todo.dto.TodoRequestDto;
import com.study.todo.todo.dto.TodoResponseDto;
import com.study.todo.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser() {
        UserRequestDto userRequestDto = createMemberRequestDto("testuser", "테스트유저");
        UserResponseDto userResponseDto = userService.signup(userRequestDto);
        return userRepository.findById(userResponseDto.getId()).orElseThrow();
    }

    private UserRequestDto createMemberRequestDto(String username, String nickname) {
        return UserRequestDto.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();
    }

    private TodoRequestDto createTodoRequestDto(Long userId, String title, String description) {
        return TodoRequestDto.builder()
                .userId(userId)
                .title(title)
                .description(description)
                .build();
    }

    @Test
    @DisplayName("회원은 TODO List를 작성할 수 있어야 한다")
    public void testCreateTodo() {
        User user = createUser();
        TodoRequestDto todoRequestDto = createTodoRequestDto(user.getId(), "Test Todo", "This is a test todo");

        TodoResponseDto createdTodo = todoService.createTodo(todoRequestDto);

        assertThat(createdTodo).isNotNull();
        assertThat(createdTodo.getTitle()).isEqualTo("Test Todo");
        assertThat(createdTodo.getDescription()).isEqualTo("This is a test todo");
        assertThat(createdTodo.getStatus()).isEqualTo(TodoStatus.TODO);
    }

    @Test
    @DisplayName("회원은 작성한 TODO List를 조회할 수 있어야 한다 - 전체 목록")
    public void testGetAllTodos() {
        User user = createUser();
        todoService.createTodo(createTodoRequestDto(user.getId(), "Todo 1", "Description 1"));
        todoService.createTodo(createTodoRequestDto(user.getId(), "Todo 2", "Description 2"));

        List<TodoResponseDto> todos = todoService.getAllTodos(user.getId());

        assertThat(todos).hasSize(2);
    }

    @Test
    @DisplayName("회원은 작성한 TODO List를 조회할 수 있어야 한다 - 가장 최근의 TODO 1개")
    public void testGetMostRecentTodo() {
        User user = createUser();

        todoService.createTodo(createTodoRequestDto(user.getId(), "Todo 1", "Description 1"));
        todoService.createTodo(createTodoRequestDto(user.getId(), "Todo 2", "Description 2"));
        TodoResponseDto mostRecentTodo = todoService.createTodo(createTodoRequestDto(user.getId(), "Todo 3", "Description 3"));

        TodoResponseDto recentTodo = todoService.getMostRecentTodo(user.getId());

        assertThat(recentTodo).isNotNull();
        assertThat(recentTodo.getId()).isEqualTo(mostRecentTodo.getId());
        assertThat(recentTodo.getCreatedAt()).isAfterOrEqualTo(mostRecentTodo.getCreatedAt());
        assertThat(recentTodo.getTitle()).isEqualTo("Todo 3");
        assertThat(recentTodo.getDescription()).isEqualTo("Description 3");
    }

    @Test
    @DisplayName("회원은 작성한 TODO List의 상태를 변경할 수 있어야 한다")
    public void testUpdateTodoStatus() {
        User user = createUser();
        TodoResponseDto todo = todoService.createTodo(createTodoRequestDto(user.getId(), "Test Todo", "This is a test todo"));

        TodoResponseDto updatedTodo = todoService.updateTodoStatus(todo.getId(), TodoStatus.IN_PROGRESS);

        assertThat(updatedTodo.getStatus()).isEqualTo(TodoStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("회원은 작성한 TODO List의 상태를 대기로 변경할 수 있어야 한다")
    public void testUpdateTodoStatusToPending() {
        User user = createUser();
        TodoResponseDto todo = todoService.createTodo(createTodoRequestDto(user.getId(), "Test Todo", "This is a test todo"));

        todoService.updateTodoStatus(todo.getId(), TodoStatus.IN_PROGRESS);
        TodoResponseDto updatedTodo = todoService.updateTodoStatus(todo.getId(), TodoStatus.PENDING);

        assertThat(updatedTodo.getStatus()).isEqualTo(TodoStatus.PENDING);
    }

    @Test
    @DisplayName("회원은 대기 상태에서 TODO List의 상태를 변경할 수 있어야 한다")
    public void testUpdatePendingTodoStatus() {
        User user = createUser();
        TodoResponseDto todo = todoService.createTodo(createTodoRequestDto(user.getId(), "Test Todo", "This is a test todo"));

        todoService.updateTodoStatus(todo.getId(), TodoStatus.IN_PROGRESS);
        todoService.updateTodoStatus(todo.getId(), TodoStatus.PENDING);
        TodoResponseDto updatedTodo = todoService.updateTodoStatus(todo.getId(), TodoStatus.TODO);

        assertThat(updatedTodo.getStatus()).isEqualTo(TodoStatus.TODO);
    }

    @Test
    @DisplayName("진행 중 상태가 아닌 TODO를 대기 상태로 변경할 수 없다")
    public void testCannotUpdateTodoStatusToPendingFromNonInProgress() {
        User user = createUser();
        TodoResponseDto todo = todoService.createTodo(createTodoRequestDto(user.getId(), "Test Todo", "This is a test todo"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            todoService.updateTodoStatus(todo.getId(), TodoStatus.PENDING);
        });

        assertThat(exception.getMessage()).isEqualTo("Can only change to PENDING from IN_PROGRESS");
    }
}