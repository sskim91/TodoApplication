package com.study.todo.user.service;

import com.study.todo.user.domain.User;
import com.study.todo.user.dto.UserRequestDto;
import com.study.todo.user.dto.UserResponseDto;
import com.study.todo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto signup(UserRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            log.info("User name = {}", requestDto.getUsername());
            throw new IllegalStateException("Username already exists");
        }

        if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
            log.info("User Nickname = {} ", requestDto.getNickname());
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
