package site.tteolione.tteolione.api.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.service.user.request.ChangeNicknameServiceReq;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.SecurityUtils;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

//    @BeforeEach
//    void init() {
//        userService = new UserService(userRepository);
//    }

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

    @DisplayName("닉네임 중복이 없을때 false 반환")
    @Test
    void existByNicknameFalse() {
        // given
        String findNickname = "test123456";
        User user1 = createUser("test123", "test123@naver.com", "test123");
        User user2 = createUser("test1234", "test1234@naver.com", "test1234");
        User user3 = createUser("test1235", "test12345@naver.com", "test12345");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userService.existByNickname(findNickname);

        // then
        Assertions.assertThat(result).isFalse();
    }

    @DisplayName("닉네임 중복이 없을때 true 반환")
    @Test
    void existByNicknameTrue() {
        // given
        String findNickname = "test123";
        User user1 = createUser("test123", "test123@naver.com", "test123");
        User user2 = createUser("test1234", "test1234@naver.com", "test1234");
        User user3 = createUser("test1235", "test12345@naver.com", "test12345");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        boolean result = userService.existByNickname(findNickname);

        // then
        Assertions.assertThat(result).isTrue();
    }

    @DisplayName("userId로 조회했는데 존재하는 회원일 때")
    @Test
    void findById() {
        //given
        User user1 = createUser("test123", "test123@naver.com");
        User user2 = createUser("test1234", "test1234@naver.com");
        User user3 = createUser("test1235", "test12345@naver.com");
        userRepository.saveAllAndFlush(List.of(user1, user2, user3));

        // when
        User user = userService.findById(user1.getUserId());

        // then
        Assertions.assertThat(user.getUserId()).isEqualTo(user1.getUserId());
        Assertions.assertThat(user.getEmail()).isEqualTo(user1.getEmail());
    }

    @DisplayName("userId로 조회했는데 존재하지 않는 회원일 때")
    @Test
    void findByIdNotExist() {
        //given
        User user1 = createUser("test123", "test123@naver.com");
        User user2 = createUser("test1234", "test1234@naver.com");
        User user3 = createUser("test1235", "test12345@naver.com");
        userRepository.saveAll(List.of(user1, user2, user3));

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findById(4L);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_EXISTS_USER);
    }

    @DisplayName("회원의 닉네임 변경이 기존의 것과 일치하지 않고 다른 유저들의 것과 일치하지 않을 때 - 성공")
    @Test
    void changeNickname_Success() {
        // given
        String newNickname = "newNickname";

        User user = createUser("test123", "test123@naver.com", "originNickname");
        userRepository.save(user);

        SecurityUserDto userDto = SecurityUserDto.builder()
                .userId(user.getUserId())
                .userRole(EAuthority.ROLE_USER)
                .build();

        ChangeNicknameServiceReq request = ChangeNicknameServiceReq.builder()
                .nickname(newNickname)
                .build();

        // when
        userService.changeNickname(userDto, request);
        User findUser = userRepository.findById(user.getUserId()).get();

        // then
        Assertions.assertThat(findUser.getNickname()).isEqualTo(newNickname);
    }

    @DisplayName("회원의 닉네임 변경이 기존의 것 일치할 때 예외처리 - 실패")
    @Test
    void changeNickname_EqualsToOriginNickname_True() {
        // given
        String newNickname = "originNickname";

        User user = createUser("test123", "test123@naver.com", "originNickname");
        userRepository.save(user);

        SecurityUserDto userDto = SecurityUserDto.builder()
                .userId(user.getUserId())
                .userRole(EAuthority.ROLE_USER)
                .build();

        ChangeNicknameServiceReq request = ChangeNicknameServiceReq.builder()
                .nickname(newNickname)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.changeNickname(userDto, request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.EQUALS_NICKNAME);
    }

    @DisplayName("변경할 닉네임이 다른 유저들의 것과 일치할 때 예외처 - 실패")
    @Test
    void changeNickname_ExistByNickname_True() {
        // given
        String user2Nickname = "user2Nickname";

        User user1 = createUser("test123", "test123@naver.com", "originNickname");
        User user2 = createUser("test123", "test123@naver.com", user2Nickname);
        userRepository.saveAll(List.of(user1, user2));

        SecurityUserDto userDto = SecurityUserDto.builder()
                .userId(user1.getUserId())
                .userRole(EAuthority.ROLE_USER)
                .build();

        ChangeNicknameServiceReq request = ChangeNicknameServiceReq.builder()
                .nickname(user2Nickname)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.changeNickname(userDto, request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.EXIST_NICKNAME);
    }

    private User createUser(String username, String email) {
        return User.builder()
                .loginId(username)
                .email(email)
                .build();
    }

    private User createUser(String username, String email, String nickname) {
        return User.builder()
                .loginId(username)
                .email(email)
                .nickname(nickname)
                .build();
    }

}