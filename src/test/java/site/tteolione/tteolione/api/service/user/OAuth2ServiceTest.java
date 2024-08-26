package site.tteolione.tteolione.api.service.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.IntegrationTestSupport;
import site.tteolione.tteolione.api.service.user.request.OAuth2KakaoServiceReq;
import site.tteolione.tteolione.api.service.user.response.LoginRes;
import site.tteolione.tteolione.client.oauth2.kakao.KakaoAccount;
import site.tteolione.tteolione.client.oauth2.kakao.KakaoAuthClient;
import site.tteolione.tteolione.client.oauth2.kakao.Profile;
import site.tteolione.tteolione.client.oauth2.kakao.Properties;
import site.tteolione.tteolione.client.oauth2.kakao.response.KakaoUserInfoRes;
import site.tteolione.tteolione.client.s3.S3ImageService;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.jwt.TokenProvider;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2ServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KakaoAuthClient kakaoAuthClient;

    @MockBean
    private S3ImageService s3ImageService;

    @Autowired
    private OAuth2Service oAuth2Service;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("카카오 로그인 - 이미 회원인 경우 로그인 처리")
    @Test
    void validateKakaoAccessToken_ExistUser() {
        // given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String nickname = "test123";
        String email = "test123@naver.com";
        String loginId = "test123@naver.com";

        User user = User.builder()
                .loginType(ELoginType.eKakao)
                .loginId(loginId)
                .email(email)
                .userRole(EAuthority.ROLE_USER)
                .build();
        userRepository.save(user);

        OAuth2KakaoServiceReq request = OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        KakaoUserInfoRes kakaoInfoRes = createKakaoUserInfoRes(nickname, email);

        BDDMockito.when(kakaoAuthClient.getUserInfo("Bearer " + accessToken)).thenReturn(kakaoInfoRes);

        // when
        LoginRes loginRes = oAuth2Service.validateKakaoAccessToken(request);

        // then
        Assertions.assertThat(loginRes.isExistsUser()).isTrue();
        Assertions.assertThat(loginRes.getUserId()).isEqualTo(user.getUserId());
        Assertions.assertThat(loginRes.getNickname()).isEqualTo(user.getNickname());
    }

    @DisplayName("카카오 로그인 - 회원가입인 경우 로그인 처리하지않음(existUser를 false 반환하여 프로필로 이동)")
    @Test
    void validateKakaoAccessToken_Not_ExistUser() {
        // given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String nickname = "test123";
        String email = "test123@naver.com";
        String loginId = "test123@naver.com";

        OAuth2KakaoServiceReq request = OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        KakaoUserInfoRes kakaoInfoRes = createKakaoUserInfoRes(nickname, email);

        BDDMockito.when(kakaoAuthClient.getUserInfo("Bearer " + accessToken)).thenReturn(kakaoInfoRes);

        // when
        LoginRes loginRes = oAuth2Service.validateKakaoAccessToken(request);

        // then
        Assertions.assertThat(loginRes.isExistsUser()).isFalse();
        Assertions.assertThat(loginRes.getUserId()).isNull();
        Assertions.assertThat(loginRes.getNickname()).isNull();
        Assertions.assertThat(loginRes.getAccessToken()).isNull();
        Assertions.assertThat(loginRes.getRefreshToken()).isNull();
    }


    @DisplayName("카카오 로그인 - 탈퇴한 회원일 경우 예외처리")
    @Test
    void validateKakaoAccessToken_Exist_Withdraw_User() {
        // given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String nickname = "test123";
        String email = "test123@naver.com";
        String loginId = "test123@naver.com";

        User user = User.builder()
                .loginType(ELoginType.eKakao)
                .loginId(loginId)
                .email(email)
                .userRole(EAuthority.ROLE_WITHDRAW_USER)
                .build();
        userRepository.save(user);

        OAuth2KakaoServiceReq request = OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        KakaoUserInfoRes kakaoInfoRes = createKakaoUserInfoRes(nickname, email);

        BDDMockito.when(kakaoAuthClient.getUserInfo("Bearer " + accessToken)).thenReturn(kakaoInfoRes);

        // when
        // then
        Assertions.assertThatThrownBy(() -> oAuth2Service.validateKakaoAccessToken(request))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.WITH_DRAW_USER.getMessage());
    }

    @DisplayName("존재하지 않는 유저일때 - 카카오로 회원가입")
    @Test
    void signUpKakao_Not_Exist_User() {
        // given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String nickname = "test123";
        String email = "test123@naver.com";

        KakaoUserInfoRes kakaoUserInfoRes = createKakaoUserInfoRes(nickname, email);
        MultipartFile profile = Mockito.mock(MultipartFile.class);


        BDDMockito.when(kakaoAuthClient.getUserInfo("Bearer " + accessToken)).thenReturn(kakaoUserInfoRes);
        BDDMockito.when(s3ImageService.upload(profile)).thenReturn("1.jpg");

        OAuth2KakaoServiceReq request = OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();


        // when
        LoginRes loginRes = oAuth2Service.signUpKakao(profile, request);

        // then
        Assertions.assertThat(loginRes.isExistsUser()).isTrue();
        Assertions.assertThat(loginRes.getAccessToken()).isNotNull();
        Assertions.assertThat(loginRes.getRefreshToken()).isNotNull();
        Assertions.assertThat(loginRes.getAppleRefreshToken()).isNull();
    }

    @DisplayName("카카오 로그인 - 탈퇴한 회원일 경우 예외처리")
    @Test
    void signUpKakao_Exist_User() {
        // given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String nickname = "test123";
        String email = "test123@naver.com";
        String loginId = "test123@naver.com";

        User user = User.builder()
                .loginType(ELoginType.eKakao)
                .loginId(loginId)
                .email(email)
                .userRole(EAuthority.ROLE_USER)
                .build();
        userRepository.save(user);

        OAuth2KakaoServiceReq request = OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        KakaoUserInfoRes kakaoInfoRes = createKakaoUserInfoRes(nickname, email);
        MultipartFile profile = Mockito.mock(MultipartFile.class);


        BDDMockito.when(kakaoAuthClient.getUserInfo("Bearer " + accessToken)).thenReturn(kakaoInfoRes);

        // when
        // then
        Assertions.assertThatThrownBy(() -> oAuth2Service.signUpKakao(profile, request))
                .isInstanceOf(GeneralException.class)
                .hasMessage(Code.EXISTS_USER.getMessage());
    }


    private KakaoUserInfoRes createKakaoUserInfoRes(String nickname, String email) {
        Profile profile = Profile.builder()
                .nickname(nickname)
                .profileImageUrl("1.jpg")
                .build();

        KakaoAccount kakaoAccount = KakaoAccount.builder()
                .email(email)
                .profile(profile)
                .build();

        Properties properties = Properties.builder()
                .nickname(nickname)
                .build();

        return KakaoUserInfoRes.builder()
                .id("1")
                .properties(properties)
                .kakaoAccount(kakaoAccount)
                .build();
    }

}
