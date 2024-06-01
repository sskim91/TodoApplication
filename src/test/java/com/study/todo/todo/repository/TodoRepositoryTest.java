package com.study.todo.todo.repository;

import com.study.todo.user.domain.User;
import com.study.todo.user.repository.UserRepository;
import com.study.todo.todo.domain.Todo;
import com.study.todo.todo.domain.TodoStatus;
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

    User user;

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build());
    }

    private Todo createTodo(String title, String description) {
        return Todo.builder()
                .title(title)
                .description(description)
                .status(TodoStatus.TODO)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("TODO 저장 및 조회 테스트")
    public void testSaveAndFindTodo() {
        Todo todo = createTodo("테스트 제목", "테스트 내용");
        todoRepository.save(todo);

        List<Todo> todos = todoRepository.findByUser(user);
        assertThat(todos).hasSize(1);

        Todo foundTodo = todos.get(0);
        assertThat(foundTodo.getTitle()).isEqualTo("테스트 제목");
        assertThat(foundTodo.getDescription()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("가장 최근에 작성된 TODO 조회 테스트")
    public void testFindMostRecentTodo() {
        todoRepository.save(createTodo("Todo 1", "Description 1"));
        todoRepository.save(createTodo("Todo 2", "Description 2"));
        Todo mostRecentTodo = todoRepository.save(createTodo("Todo 3", "Description 3"));

        Todo foundTodo = todoRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
        assertThat(foundTodo).isNotNull();
        assertThat(foundTodo.getTitle()).isEqualTo("Todo 3");
        assertThat(foundTodo.getDescription()).isEqualTo("Description 3");
        assertThat(foundTodo.getId()).isEqualTo(mostRecentTodo.getId());
    }
}