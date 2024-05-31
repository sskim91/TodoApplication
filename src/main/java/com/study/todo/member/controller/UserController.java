package com.study.todo.member.controller;

import com.study.todo.member.dto.UserRequestDto;
import com.study.todo.member.dto.UserResponseDto;
import com.study.todo.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public UserResponseDto signup(@RequestBody UserRequestDto requestDto) {
        return userService.signup(requestDto);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody UserRequestDto requestDto) {
        return userService.login(requestDto);
    }

    @DeleteMapping("/{id}")
    public void withdraw(@PathVariable Long id) {
        userService.withdraw(id);
    }
}
