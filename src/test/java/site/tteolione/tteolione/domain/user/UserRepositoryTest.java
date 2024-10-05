package site.tteolione.tteolione.domain.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 이메일로 회원을 조회한다.")
    @Test
    void findByEmail() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test123", "test1234@naver.com", "");
        User user3 = createUser("test123", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        //when
        Optional<User> findUser_ = userRepository.findByEmail("test123@naver.com");
        User findUser = findUser_.get();

        //then
        assertThat(findUser).isEqualTo(user1);
    }


    @DisplayName("존재하는 이메일이 있을 떄 true 반환한다.")
    @Test
    void existsByEmailTrue() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test1234", "test1234@naver.com", "");
        User user3 = createUser("test1235", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        //when
        boolean existUser = userRepository.existsByEmail("test123@naver.com");

        //then
        assertThat(existUser).isEqualTo(true);
    }

    @DisplayName("존재하는 이메일이 있을 떄 false 반환한다.")
    @Test
    void existsByEmailFalse() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test1234", "test1234@naver.com", "");
        User user3 = createUser("test1235", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        //when
        boolean existUser = userRepository.existsByEmail("test123@google.com");

        //then
        assertThat(existUser).isEqualTo(false);
    }

    @DisplayName("로그인 Id 로 권한과 함께 User 를 가져온다.")
    @Test
    void findOneWithAuthoritiesByLoginId() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test1234", "test1234@naver.com", "");
        User user3 = createUser("test1235", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        //when
        Optional<User> findUser_ = userRepository.findByLoginId("test123");
        User findUser = findUser_.get();

        //then
        assertThat(findUser).isEqualTo(user1);
    }

    @DisplayName("회원가입시 로그인 아이디가 중복아닐때 true 반환")
    @Test
    void notExistsByLoginId() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test1234", "test1234@naver.com", "");
        User user3 = createUser("test1235", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userRepository.existsByLoginId("test123");

        // then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("회원가입시 로그인 아이디가 중복일때 false 반환")
    @Test
    void existsByLoginId() {
        //given
        User user1 = createUser("test123", "test123@naver.com", "");
        User user2 = createUser("test1234", "test1234@naver.com", "");
        User user3 = createUser("test1235", "test12345@naver.com", "");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userRepository.existsByLoginId("successId123");

        // then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("존재하는 닉네임이 없으면 false 반환")
    @Test
    void existsByNicknameFalse() {
        // given
        String findNickname = "test123456";
        User user1 = createUser("test123", "test123@naver.com", "test123");
        User user2 = createUser("test1234", "test1234@naver.com", "test1234");
        User user3 = createUser("test1235", "test12345@naver.com", "test12345");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userRepository.existsByNickname(findNickname);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("존재하는 닉네임이 있으면 false 반환")
    @Test
    void existsByNicknameTrue() {
        // given
        String findNickname = "test123";
        User user1 = createUser("test123", "test123@naver.com", findNickname);
        User user2 = createUser("test1234", "test1234@naver.com", "test1234");
        User user3 = createUser("test1235", "test12345@naver.com", "test12345");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userRepository.existsByNickname(findNickname);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("로그인 타입과 이메일이 일치하는 유저 확인")
    @Test
    void findByEmailAndLoginType() {
        // given
        String findEmail = "test123@naver.com";
        User user1 = createUser("appUser", findEmail, ELoginType.eApp);
        User user2 = createUser("kakaoUser", findEmail, ELoginType.eKakao);
        userRepository.saveAll(List.of(user1, user2));

        // when
        User findUser = userRepository.findByEmailAndLoginType(findEmail, ELoginType.eKakao).get();

        // then
        Assertions.assertThat(findUser).isEqualTo(user2);
        Assertions.assertThat(findUser.getEmail()).isEqualTo(findEmail);
        Assertions.assertThat(findUser.getLoginType()).isEqualTo(ELoginType.eKakao);
    }

    @DisplayName("이름과 이메일이 일치하는 유저 확인")
    @Test
    void findByUsernameAndEmail() {
        // given
        String username = "테스터";
        String email = "test123@naver.com";
        User saveUser = createUser(username, email);
        userRepository.save(saveUser);

        // when
        User findUser = userRepository.findByUsernameAndEmail(username, email).get();

        // then
        Assertions.assertThat(findUser).isEqualTo(saveUser);
        Assertions.assertThat(findUser.getUsername()).isEqualTo(username);
        Assertions.assertThat(findUser.getEmail()).isEqualTo(email);
    }

    private User createUser(String username, String email, String nickname) {
        return User.builder()
                .loginId(username)
                .email(email)
                .nickname(nickname)
                .build();
    }

    private User createUser(String username, String email, ELoginType loginType) {
        return User.builder()
                .loginId(username)
                .email(email)
                .loginType(loginType)
                .build();
    }

    private User createUser(String username, String email) {
        return User.builder()
                .username(username)
                .email(email)
                .build();
    }
}