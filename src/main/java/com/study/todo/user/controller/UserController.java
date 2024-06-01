package com.study.todo.user.controller;

import com.study.todo.user.dto.UserRequestDto;
import com.study.todo.user.dto.UserResponseDto;
import com.study.todo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
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
