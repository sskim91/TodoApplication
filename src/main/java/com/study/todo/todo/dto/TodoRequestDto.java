package com.study.todo.todo.dto;

import com.study.todo.todo.domain.TodoStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequestDto {
    private String title;
    private String description;
    private TodoStatus status;
}
