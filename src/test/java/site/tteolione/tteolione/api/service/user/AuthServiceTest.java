package site.tteolione.tteolione.api.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;
import site.tteolione.tteolione.client.s3.S3ImageService;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.domain.mail.EmailAuth;
import site.tteolione.tteolione.domain.mail.EmailAuthRepository;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Collections;
import java.util.Optional;

@Transactional
class AuthServiceTest extends IntegrationTestSupport {


    @Mock
    private S3ImageService s3ImageService;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;


    @AfterEach
    void tearDown() {
        emailAuthRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입 성공시 회원가입 유저 반환")
    @Test
    void signUpUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        EmailAuth emailAuth = EmailAuth.createEmailAuth("test123@naver.com");
        User expectedUser = User.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("encodedPassword")
                .profile("profile.jpg")
                .emailAuthChecked(true)
                .loginType(ELoginType.eApp)
                .activated(true)
                .build();

        BDDMockito.when(emailAuthRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(emailAuth));
        BDDMockito.when(s3ImageService.upload(profile)).thenReturn("profile.jpg");
        BDDMockito.when(passwordEncoder.encode("testPassword!")).thenReturn("encodedPassword");
        BDDMockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(expectedUser);

        // when
        User actualUser = authService.signUpUser(request, profile);

        // then
        Assertions.assertThat(actualUser).isNotNull();
        Assertions.assertThat(actualUser.getLoginId()).isEqualTo(expectedUser.getLoginId());
        Assertions.assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
        Assertions.assertThat(actualUser.getUsername()).isEqualTo(expectedUser.getUsername());
        Assertions.assertThat(actualUser.getNickname()).isEqualTo(expectedUser.getNickname());
        Assertions.assertThat(actualUser.getProfile()).isEqualTo(expectedUser.getProfile());
        Assertions.assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
        Assertions.assertThat(actualUser.isEmailAuthChecked()).isTrue();

        BDDMockito.verify(emailAuthRepository, Mockito.times(1)).findByEmail("test123@naver.com");
        BDDMockito.verify(s3ImageService, Mockito.times(1)).upload(profile);
        BDDMockito.verify(passwordEncoder, Mockito.times(1)).encode("testPassword1!");
        BDDMockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        BDDMockito.verify(emailAuthRepository, Mockito.times(1)).deleteByEmail("test123@naver.com");
    }

    @DisplayName("이미 등록된 앱 자체 회원인지 확인 - eApp 유저 존재 시 예외 발생")
    @Test
    void signUpUser_existingEAppUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        User existingUser = User.builder()
                .email("test123@naver.com")
                .emailAuthChecked(true)
                .loginType(ELoginType.eApp)
                .build();

        BDDMockito.when(userRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(existingUser));

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.signUpUser(request, profile))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_USER.getMessage());
    }

    @DisplayName("이미 등록된 카카오 회원인지 확인 - eKakao 유저 존재 시 예외 발생")
    @Test
    void signUpUser_existingKakaoUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        User existingUser = User.builder()
                .email("test123@naver.com")
                .emailAuthChecked(true)
                .loginType(ELoginType.eKakao)
                .build();

        BDDMockito.when(userRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(existingUser));

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.signUpUser(request, profile))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_KAKAO.getMessage());
    }

    @DisplayName("이미 등록된 구글 회원인지 확인 - eGoogle 유저 존재 시 예외 발생")
    @Test
    void signUpUser_existingGoogleUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        User existingUser = User.builder()
                .email("test123@naver.com")
                .emailAuthChecked(true)
                .loginType(ELoginType.eGoogle)
                .build();

        BDDMockito.when(userRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(existingUser));

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.signUpUser(request, profile))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_GOOGLE.getMessage());
    }

    @DisplayName("이미 등록된 애플 회원인지 확인 - eApple 유저 존재 시 예외 발생")
    @Test
    void signUpUser_existingAppleUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        User existingUser = User.builder()
                .email("test123@naver.com")
                .emailAuthChecked(true)
                .loginType(ELoginType.eApple)
                .build();

        BDDMockito.when(userRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(existingUser));

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.signUpUser(request, profile))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_APPLE.getMessage());
    }

    @DisplayName("이미 등록된 네이버 회원인지 확인 - eNaver 유저 존재 시 예외 발생")
    @Test
    void signUpUser_existingNaverUser() {
        // given
        SignUpServiceReq request = SignUpServiceReq.builder()
                .loginId("testLoginId")
                .email("test123@naver.com")
                .username("testUsername")
                .nickname("testNickname")
                .password("testPassword1!")
                .build();

        MultipartFile profile = new MockMultipartFile("profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        User existingUser = User.builder()
                .email("test123@naver.com")
                .emailAuthChecked(true)
                .loginType(ELoginType.eNaver)
                .build();

        BDDMockito.when(userRepository.findByEmail("test123@naver.com")).thenReturn(Optional.of(existingUser));

        // when
        // then
        Assertions.assertThatThrownBy(() -> authService.signUpUser(request, profile))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_NAVER.getMessage());
    }

}