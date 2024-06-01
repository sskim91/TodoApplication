package com.study.todo.todo.repository;

import com.study.todo.user.domain.User;
import com.study.todo.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUser(User user);

    Optional<Todo> findTopByUserOrderByCreatedAtDesc(User user);

}
