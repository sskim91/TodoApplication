package com.study.todo.member.service;

import com.study.todo.member.domain.User;
import com.study.todo.member.dto.UserRequestDto;
import com.study.todo.member.dto.UserResponseDto;
import com.study.todo.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto signup(UserRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            throw new IllegalStateException("Nickname already exists");
        }

        final User user = requestDto.toUser();
        user.encodePassword(passwordEncoder); // 비밀번호 인코딩

        final User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }

    public UserResponseDto login(UserRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return UserResponseDto.from(user);
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member id: " + id));

        userRepository.delete(user);
    }
}
