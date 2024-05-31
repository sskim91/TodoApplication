package com.study.todo.member.service;

import com.study.todo.member.domain.User;
import com.study.todo.member.dto.UserRequestDto;
import com.study.todo.member.dto.UserResponseDto;
import com.study.todo.member.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 테스트")
    public void testSignup() {
        UserRequestDto userRequestDto = createMemberRequestDto();

        UserResponseDto savedUser = userService.signup(userRequestDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getNickname()).isEqualTo("테스트유저");
        assertThat(userRepository.findByUsername("testuser")).isPresent();
    }

    @Test
    @DisplayName("회원 가입 시 중복 사용자명 확인 테스트")
    public void testSignupWithDuplicateUsername() {
        UserRequestDto userRequestDto = createMemberRequestDto();

        userService.signup(userRequestDto);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.signup(userRequestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Username already exists");
    }

    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() {
        User user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        UserRequestDto loginRequestDto = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .build();

        UserResponseDto loggedInUser = userService.login(loginRequestDto);

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getUsername()).isEqualTo("testuser");
        assertThat(loggedInUser.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 잘못된 비밀번호")
    public void testLoginWithInvalidPassword() {
        User user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        UserRequestDto loginRequestDto = UserRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.login(loginRequestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    public void testWithdraw() {
        User user = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("password"))
                .nickname("테스트유저")
                .build();
        userRepository.save(user);

        userService.withdraw(user.getId());

        assertThat(userRepository.findById(user.getId())).isNotPresent();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 존재하지 않는 사용자 ID")
    public void testWithdrawWithInvalidId() {
        Long invalidId = 999L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.withdraw(invalidId);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid member id: " + invalidId);
    }

    private UserRequestDto createMemberRequestDto() {
        return UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .nickname("테스트유저")
                .build();
    }
}