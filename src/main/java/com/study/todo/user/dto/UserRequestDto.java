package com.study.todo.user.dto;

import com.study.todo.user.domain.User;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {
    private String username;
    private String password;
    private String nickname;

    public User toUser() {
        return User.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .build();
    }
}
