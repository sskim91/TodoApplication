package com.study.todo.todo.repository;

import com.study.todo.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUserId(Long userId, Pageable pageable);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
