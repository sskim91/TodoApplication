package com.study.todo.user.service;

import com.study.todo.user.domain.User;
import com.study.todo.user.dto.UserRequestDto;
import com.study.todo.user.dto.UserResponseDto;
import com.study.todo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    private UserRequestDto createMemberRequestDto(String username, String nickname) {
        return UserRequestDto.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();
    }

    @Test
    @DisplayName("회원 가입 테스트")
    public void testSignup() {
        UserRequestDto userRequestDto = createMemberRequestDto("testuser", "테스트유저");

        // UserRepository의 findByUsername 메서드가 "testuser" 인수와 함께 호출되었을 때
        // Optional.empty()를 반환하도록 설정
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // UserRepository의 findByNickname 메서드가 "테스트유저" 인수와 함께 호출되었을 때
        // Optional.empty()를 반환하도록 설정
        when(userRepository.findByNickname("테스트유저")).thenReturn(Optional.empty());

        // PasswordEncoder의 encode 메서드가 "password" 인수와 함께 호출되었을 때
        // "encodedPassword"를 반환하도록 설정
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        //invocation.getArgument(0)는 메서드 호출 시 전달된 첫 번째 인수를 반환
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 회원 가입을 수행
        UserResponseDto savedUser = userService.signup(userRequestDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("회원 가입 시 중복 사용자명 확인 테스트")
    public void testSignupWithDuplicateUsername() {
        UserRequestDto userRequestDto = createMemberRequestDto("testuser", "테스트유저");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(User.builder().build()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.signup(userRequestDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Username already exists");
    }

    @Test
    @DisplayName("회원 가입 시 중복 닉네임 확인 테스트")
    public void testSignupWithDuplicateNickname() {
        UserRequestDto userRequestDto1 = createMemberRequestDto("testuser1", "테스트유저");
        UserRequestDto userRequestDto2 = createMemberRequestDto("testuser2", "테스트유저");

        when(userRepository.findByUsername("testuser1")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("testuser2")).thenReturn(Optional.empty());
        when(userRepository.findByNickname("테스트유저")).thenReturn(Optional.of(User.builder().build()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userService.signup(userRequestDto2);
        });

        assertThat(exception.getMessage()).isEqualTo("Nickname already exists");
    }

    @Test
    @DisplayName("로그인 테스트")
    public void testLogin() {
        User user = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

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
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

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
                .password("encodedPassword")
                .nickname("테스트유저")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.withdraw(user.getId());

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트 - 존재하지 않는 사용자 ID")
    public void testWithdrawWithInvalidId() {
        Long invalidId = 999L;

        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.withdraw(invalidId);
        });

        assertThat(exception.getMessage()).isEqualTo("Invalid member id: " + invalidId);
    }
}