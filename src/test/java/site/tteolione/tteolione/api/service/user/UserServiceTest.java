package site.tteolione.tteolione.api.service.user;

import jakarta.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.api.service.user.request.*;
import site.tteolione.tteolione.api.service.user.response.VerifyLoginIdRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private EmailService emailService;

    @Autowired
    private RedisUtil redisUtil;

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

        long nonExistentUserId = user3.getUserId() + 1;

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findById(nonExistentUserId);
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

    @DisplayName("아이디찾기시 회원의 유저네임과 이메일이 일치하면 이메일 인증번호 전송 - 성공")
    @Test
    void findLoginId_Success() throws MessagingException {
        // given
        String username = "테스터";
        String email = "test123@naver.com";

        User saveUser = createUserWithUsernameAndEmailAndLoginType(username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        FindServiceLoginIdReq request = FindServiceLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // 이메일 발송 성공하도록 설정
        BDDMockito.when(emailService.sendEmail(email)).thenReturn(true);

        // when
        String result = userService.findLoginId(request);

        // then
        Assertions.assertThat(result).isEqualTo("이메일 인증코드 발송에 성공했습니다.");
    }

    @DisplayName("아이디 찾기시 회원 유저네임 또는 이메일이 틀릴 때 예외처리  - 실패")
    @Test
    void findLoginId_NotExistByUser() {
        // given
        String username = "테스터";
        String email = "test123@naver.com";
        String testEmail = "test12345@naver.com";

        User saveUser = createUserWithUsernameAndEmailAndLoginType(username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        FindServiceLoginIdReq request = FindServiceLoginIdReq.builder()
                .username(username)
                .email(testEmail)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findLoginId(request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_FOUND_USER_INFO);
    }

    @DisplayName("아이디 찾기시 앱 자체 로그인 회원이 아닐 때 예외처리 - 실패")
    @Test
    void findLoginId_NotEqualsEApp() {
        // given
        String username = "테스터";
        String email = "test123@naver.com";

        User saveUser = createUserWithUsernameAndEmailAndLoginType(username, email, ELoginType.eKakao);
        userRepository.save(saveUser);

        FindServiceLoginIdReq request = FindServiceLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findLoginId(request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.FOUND_KAKAO_USER);
    }

    @DisplayName("아이디 찾기 검증 시 이메일 검증 번호 인증 성공이면 유저의 loginId 반환 - 성공")
    @Test
    void verifyLoginId_Success() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServiceLoginIdReq request = VerifyServiceLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(true);

        // when
        VerifyLoginIdRes response = userService.verifyLoginId(request);
        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(response.getLoginId()).isEqualTo(loginId);
    }

    @DisplayName("아이디 찾기 검증 시 이메일 검증 번호가 틀리면 예외처리 - 실패")
    @Test
    void verifyLoginId_NotEqualsAuthCode() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServiceLoginIdReq request = VerifyServiceLoginIdReq.builder()
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(false);

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.verifyLoginId(request);
        });

        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.VERIFY_EMAIL_CODE);
    }

    @DisplayName("아이디 찾기 검증 시 이메일 검증 번호는 맞지만 유저네임이 틀리면 예외처리 - 실패")
    @Test
    void verifyLoginId_NotEquals_Username_Or_Email() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        String notEqualsUsername = "틀린테스터명";
        VerifyServiceLoginIdReq request = VerifyServiceLoginIdReq.builder()
                .username(notEqualsUsername)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(true);

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.verifyLoginId(request);
        });

        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_FOUND_USER_INFO);
    }

    @DisplayName("비밀번호 찾기 시 회원의 로그인Id, 유저네임, 이메일이 일치하면 이메일 인증번호 전송 - 성공")
    @Test
    void findPassword_Success() throws MessagingException {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";

        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        FindServicePasswordReq request = FindServicePasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // 이메일 발송 성공하도록 설정
        BDDMockito.when(emailService.sendEmail(email)).thenReturn(true);

        // when
        String result = userService.findPassword(request);

        // then
        Assertions.assertThat(result).isEqualTo("이메일 인증코드 발송에 성공했습니다.");
    }

    @DisplayName("비밀번호 찾기 시 앱 자체 로그인 회원이 아닐 때 예외처리 - 실패")
    @Test
    void findPassword_NotEquals_EApp() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";

        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eKakao);
        userRepository.save(saveUser);

        FindServicePasswordReq request = FindServicePasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findPassword(request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.FOUND_KAKAO_USER);
    }

    @DisplayName("비밀번호 찾기 시 로그인 Id, 유저네임, 이메일이 일치하지 않을 때 예외처리 - 실패")
    @Test
    void findPassword_NotExistBy_User() {
        // given
        String loginId = "test123";
        String notExistByLoginId = "noMatch12";
        String username = "테스터";
        String email = "test123@naver.com";

        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        FindServicePasswordReq request = FindServicePasswordReq.builder()
                .loginId(notExistByLoginId)
                .username(username)
                .email(email)
                .build();

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.findPassword(request);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_FOUND_USER_INFO);
    }

    @DisplayName("비밀번호 찾기 검증 시 이메일 검증 번호 인증 성공이면 검증 성공 응답 메시지 반환 - 성공")
    @Test
    void verifyPassword_Success() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServicePasswordReq request = VerifyServicePasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(true);

        // when
        String response = userService.verifyPassword(request);
        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(response).isEqualTo("비밀번호 이메일 검증 성공");
    }

    @DisplayName("비민번호 찾기 검증 시 이메일 검증 번호가 틀리면 예외처리 - 실패")
    @Test
    void verifyPassword_NotEqualsAuthCode() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServicePasswordReq request = VerifyServicePasswordReq.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(false);

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.verifyPassword(request);
        });

        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.VERIFY_EMAIL_CODE);
    }

    @DisplayName("아이디 찾기 검증 시 이메일 검증 번호는 맞지만 유저네임이 틀리면 예외처리 - 실패")
    @Test
    void verifyPassword_NotEquals_Username() {
        // given
        String loginId = "test123";
        String username = "테스터";
        String notMatchUsername = "일치하지않는테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServicePasswordReq request = VerifyServicePasswordReq.builder()
                .loginId(loginId)
                .username(notMatchUsername)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(true);

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.verifyPassword(request);
        });

        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_FOUND_USER_INFO);
    }

    @DisplayName("아이디 찾기 검증 시 이메일 검증 번호는 맞지만 로그인 ID가 틀리면 예외처리 - 실패")
    @Test
    void verifyPassword_NotEquals_LoginId() {
        // given
        String loginId = "test123";
        String notMatchLoginId = "tttt123";
        String username = "테스터";
        String email = "test123@naver.com";
        String authCode = createCode();
        User saveUser = createUserWithLoginIdAndUsernameAndEmailAndLoginType(loginId, username, email, ELoginType.eApp);
        userRepository.save(saveUser);

        VerifyServicePasswordReq request = VerifyServicePasswordReq.builder()
                .loginId(notMatchLoginId)
                .username(username)
                .email(email)
                .authCode(authCode)
                .build();

        redisUtil.setDataExpire("code:" + email, authCode, 60 * 5L);
        BDDMockito.when(emailService.verifyEmailCode(email, authCode, authCode)).thenReturn(true);

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            userService.verifyPassword(request);
        });

        redisUtil.deleteData("code:" + email);

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_FOUND_USER_INFO);
    }

    private User createUser(String loginId, String email) {
        return User.builder()
                .loginId(loginId)
                .email(email)
                .build();
    }

    private User createUser(String loginId, String email, String nickname) {
        return User.builder()
                .loginId(loginId)
                .email(email)
                .nickname(nickname)
                .build();
    }

    private User createUserWithUsernameAndEmailAndLoginType(String username, String email, ELoginType loginType) {
        return User.builder()
                .username(username)
                .email(email)
                .loginType(loginType)
                .build();
    }

    private User createUserWithLoginIdAndUsernameAndEmailAndLoginType(String loginId, String username, String email, ELoginType loginType) {
        return User.builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .loginType(loginType)
                .build();
    }

    private String createCode() {
        Random random = new Random();
        String authCode = "";

        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(25) + 65;
            authCode += (char) index;
        }

        int numIndex = random.nextInt(9000) + 1000;
        authCode += numIndex;

        return authCode;
    }

}