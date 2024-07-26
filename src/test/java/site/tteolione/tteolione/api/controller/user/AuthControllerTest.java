package site.tteolione.tteolione.api.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.api.controller.user.request.SignUpReq;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;
import site.tteolione.tteolione.config.exception.Code;
import site.tteolione.tteolione.domain.user.User;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입 성공 테스트")
    @Test
    @WithMockUser
    void signupUser() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("회원가입 성공입니다."));
    }

    @DisplayName("회원가입 실패 테스트 - 빈문자열 로그인 아이디")
    @Test
    @WithMockUser
    void signupUser_EmptyLoginId() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("회원가입 실패 테스트 - null 로그인 아이디")
    @Test
    @WithMockUser
    void signupUser_NullLoginId() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId(null)
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 로그인Id를 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - 로그인 아이디 패턴(id는 소문자 하나이상있어야하고, 6자~20자여야합니다.) ")
    @Test
    @WithMockUser
    void signupUser_PatternLoginId() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("123123123")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("id는 소문자 하나이상있어야하고, 6자~20자여야합니다."));
    }

    @DisplayName("회원가입 실패 테스트 - 빈문자열 이메일")
    @Test
    @WithMockUser
    void signupUser_EmptyEmail() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("")
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - null 이메일")
    @Test
    @WithMockUser
    void signupUser_NullEmail() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email(null)
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - 이메일 형식이 아닐때(이메일 형식이 맞지 않습니다.)")
    @Test
    @WithMockUser
    void signupUser_PatternEmail() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test.naver")
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("회원가입 실패 테스트 - 빈문자열 유저네임")
    @Test
    @WithMockUser
    void signupUser_EmptyUsername() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username("")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - null 유저네임")
    @Test
    @WithMockUser
    void signupUser_NullUsername() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("test123")
                .nickname("test12")
                .password("test123!")
                .username(null)
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이름을 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - 빈문자열 닉네임")
    @Test
    @WithMockUser
    void signupUser_EmptyNickname() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("test123")
                .nickname("")
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 닉네임을 적어주세요."));
    }

    @DisplayName("회원가입 실패 테스트 - null 닉네임")
    @Test
    @WithMockUser
    void signupUser_NullNickname() throws Exception {
        // given
        SignUpReq request = SignUpReq.builder()
                .email("test123@naver.com")
                .loginId("test123")
                .nickname(null)
                .password("test123!")
                .username("testUsername")
                .build();

        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "content".getBytes()
        );

        MockMultipartFile signUpRequest = new MockMultipartFile(
                "signUpRequest",
                "signUpRequest",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        User mockUser = Mockito.mock(User.class); // Mock User 객체 생성
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MockMultipartFile.class)))
                .thenReturn(mockUser); // Mock 객체 반환

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 닉네임을 적어주세요."));
    }

}