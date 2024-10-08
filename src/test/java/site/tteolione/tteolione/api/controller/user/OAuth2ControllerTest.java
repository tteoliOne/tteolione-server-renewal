package site.tteolione.tteolione.api.controller.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.GenerateMockToken;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.user.request.OAuth2KakaoReq;
import site.tteolione.tteolione.api.service.user.request.OAuth2KakaoServiceReq;
import site.tteolione.tteolione.api.service.user.response.LoginRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.jwt.TokenInfoRes;
import site.tteolione.tteolione.domain.user.User;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2ControllerTest extends ControllerTestSupport {

    @DisplayName("카카오 로그인시 기존회원이 아닐때")
    @Test
    @WithMockCustomAccount
    void kakaoLogin_Not_Exist_User() throws Exception {
        //given
        String accessToken = "accessToken";
        String targetToken = "targetToken";

        OAuth2KakaoReq request = OAuth2KakaoReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        LoginRes response = LoginRes.fromKakao(null, null);

        BDDMockito.when(oAuth2Service.validateKakaoAccessToken(request.toServiceRequest()))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/kakao")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.existsUser").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.appleRefreshToken").doesNotExist());
    }

    @DisplayName("카카오 로그인시 기존회원일때 로그인 처리")
    @Test
    @WithMockCustomAccount
    void kakaoLogin_Exist_User() throws Exception {
        //given
        String accessToken = "accessToken";
        String targetToken = "targetToken";
        String refreshToken = "refreshToken";
        Long userId = 1L;
        String nickname = "nickname";

        OAuth2KakaoReq request = OAuth2KakaoReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();

        TokenInfoRes tokenInfoRes = TokenInfoRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        User user = User.builder()
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(user, "userId", userId);

        LoginRes response = LoginRes.fromKakao(tokenInfoRes, user);

        BDDMockito.when(oAuth2Service.validateKakaoAccessToken(request.toServiceRequest()))
                .thenReturn(response);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/kakao")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.existsUser").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(user.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(refreshToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value(nickname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.appleRefreshToken").doesNotExist());
    }

    @DisplayName("카카오 액세스 토큰 null로 올때 유효성 검사 실패")
    @Test
    @WithMockCustomAccount
    void kakaoLogin_Failure_Validate_KakaoAccessToken() throws Exception {
        //given
        OAuth2KakaoReq request = OAuth2KakaoReq.builder()
                .accessToken(null)
                .targetToken(null)
                .build();

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v2/auth/kakao")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카카오의 accessToken을 입력해 주세요."));
    }

    @DisplayName("카카오 로그인 신규회원 등록")
    @Test
    @WithMockCustomAccount
    void kakaoSignup_Success() throws Exception {
        //given
        String kakaoAccessToken = "kakaoAccessToken";
        String fcmTargetToken = "fcmTargetToken";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String nickname = "nickname";
        Long userId = 1L;

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        OAuth2KakaoReq oAuth2KakaoReq = OAuth2KakaoReq.builder()
                .accessToken(kakaoAccessToken)
                .targetToken(fcmTargetToken)
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(oAuth2KakaoReq).getBytes()
        );

        TokenInfoRes tokenInfoRes = TokenInfoRes.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        User user = User.builder()
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(user, "userId", userId);
        LoginRes response = LoginRes.fromKakao(tokenInfoRes, user);

        BDDMockito.when(oAuth2Service.signUpKakao(profile, oAuth2KakaoReq.toServiceRequest())).thenReturn(response);

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/kakao/profile")
                        .file(profile)
                        .file(request)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.existsUser").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.accessToken").value(accessToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.refreshToken").value(refreshToken))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.nickname").value(user.getNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.appleRefreshToken").doesNotExist());
    }

    @DisplayName("카카오 로그인 신규회원 등록 - 카카오 액세스 토큰 유효성 검사 실패")
    @Test
    @WithMockCustomAccount
    void kakaoSignup_Failure_Validate_KakaoAccessToken() throws Exception {
        //given
        String accessToken = "accessToken";
        OAuth2KakaoReq oAuth2KakaoReq = OAuth2KakaoReq.builder()
                .accessToken(accessToken)
                .targetToken(null)
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(oAuth2KakaoReq).getBytes()
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/kakao/profile")
                        .file(request)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.BAD_REQUEST.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("필수 요청 파트 'profile'가 누락되었습니다."));
    }

    @DisplayName("카카오 로그인 신규회원 등록 - 카카오 액세스 토큰 유효성 검사 실패")
    @Test
    @WithMockCustomAccount
    void kakaoSignup_Miss_Profile() throws Exception {
        //given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String nickname = "nickname";
        Long userId = 1L;

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        OAuth2KakaoReq oAuth2KakaoReq = OAuth2KakaoReq.builder()
                .accessToken(null)
                .targetToken(null)
                .build();

        MockMultipartFile request = new MockMultipartFile(
                "request",
                "request",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(oAuth2KakaoReq).getBytes()
        );

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/kakao/profile")
                        .file(profile)
                        .file(request)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("카카오의 accessToken을 입력해 주세요."));
    }
}