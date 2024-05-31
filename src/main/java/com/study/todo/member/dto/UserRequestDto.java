package com.study.todo.member.dto;

import com.study.todo.member.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
