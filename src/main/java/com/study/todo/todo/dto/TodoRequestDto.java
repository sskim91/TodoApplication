package com.study.todo.todo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequestDto {
    private Long userId;
    private String title;
    private String description;
}
