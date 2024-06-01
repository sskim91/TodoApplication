package com.study.todo.user.repository;

import com.study.todo.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    private User createUser(String username, String nickname) {
        return User.builder()
                .username(username)
                .password("password")
                .nickname(nickname)
                .build();
    }

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    public void testSaveAndFindUser() {
        User user = createUser("testuser", "테스트유저");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("사용자명으로 사용자 검색 테스트")
    public void testFindByUsername() {
        User user = createUser("testuser", "테스트유저");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("닉네임으로 사용자 검색 테스트")
    public void testFindByNickname() {
        User user = createUser("testuser", "테스트유저");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByNickname("테스트유저");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickname()).isEqualTo("테스트유저");
    }

    @Test
    @DisplayName("전체 사용자 수 확인 테스트")
    public void testCountUsers() {
        User user1 = createUser("testuser1", "테스트유저1");
        User user2 = createUser("testuser2", "테스트유저2");
        userRepository.save(user1);
        userRepository.save(user2);

        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(2);
    }

}