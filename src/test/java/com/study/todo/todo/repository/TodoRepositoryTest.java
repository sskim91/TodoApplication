package com.study.todo.todo.repository;

import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
import com.study.todo.user.domain.User;
import com.study.todo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 사용자의 모든 Todo 항목을 조회할 수 있어야 한다")
    public void testFindByUserId() {
        User user = User.builder()
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        Todo todo1 = Todo.builder()
                .title("Test Todo 1")
                .description("This is test todo 1")
                .status(TodoStatus.TODO)
                .user(user)
                .build();
        Todo todo2 = Todo.builder()
                .title("Test Todo 2")
                .description("This is test todo 2")
                .status(TodoStatus.DONE)
                .user(user)
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        List<Todo> todos = todoRepository.findByUserId(user.getId());

        assertThat(todos).hasSize(2);
        assertThat(todos).extracting("title").contains("Test Todo 1", "Test Todo 2");
    }

    @Test
    @DisplayName("특정 사용자의 특정 Todo 항목을 조회할 수 있어야 한다")
    public void testFindByIdAndUserId() {
        User user = User.builder()
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        Todo todo = Todo.builder()
                .title("Test Todo")
                .description("This is a test todo")
                .status(TodoStatus.TODO)
                .user(user)
                .build();
        todoRepository.save(todo);

        Todo foundTodo = todoRepository.findByIdAndUserId(todo.getId(), user.getId())
                .orElse(null);

        assertThat(foundTodo).isNotNull();
        assertThat(foundTodo.getTitle()).isEqualTo("Test Todo");
    }

    @Test
    @DisplayName("특정 사용자의 가장 최근 Todo 항목을 조회할 수 있어야 한다")
    public void testFindFirstByUserIdOrderByCreatedAtDesc() {
        User user = User.builder()
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        Todo todo1 = Todo.builder()
                .title("Test Todo 1")
                .description("This is test todo 1")
                .status(TodoStatus.TODO)
                .user(user)
                .build();
        Todo todo2 = Todo.builder()
                .title("Test Todo 2")
                .description("This is test todo 2")
                .status(TodoStatus.DONE)
                .user(user)
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);

        Todo mostRecentTodo = todoRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());

        assertThat(mostRecentTodo).isNotNull();
        assertThat(mostRecentTodo.getTitle()).isEqualTo("Test Todo 2");
    }
}