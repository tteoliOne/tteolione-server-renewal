package site.tteolione.tteolione.api.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.config.exception.Code;
import site.tteolione.tteolione.config.exception.GeneralException;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입시 로그인 아이디가 중복아닐 때 true 반환")
    @Test
    void notDuplicateLoginId() {
        //given
        User user1 = createUser("test123", "test123@naver.com");
        User user2 = createUser("test1234", "test1234@naver.com");
        User user3 = createUser("test1235", "test12345@naver.com");
        userRepository.saveAll(List.of(user1, user2, user3));

        //when
        boolean result = userService.duplicateLoginId("success123");

        //then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("회원가입시 로그인 아이디가 중복일 때 예외처리한다.")
    @Test
    void duplicateLoginId() {
        //given
        User user1 = createUser("test123", "test123@naver.com");
        User user2 = createUser("test1234", "test1234@naver.com");
        User user3 = createUser("test1235", "test12345@naver.com");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.duplicateLoginId("test123");
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.EXISTS_LOGIN_ID);
    }

    private User createUser(String username, String email) {
        return User.builder()
                .loginId(username)
                .email(email)
                .authorities(Collections.singleton(User.toRoleUserAuthority()))
                .build();
    }

}