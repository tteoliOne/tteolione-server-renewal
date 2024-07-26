package site.tteolione.tteolione.docs.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.controller.user.AuthController;
import site.tteolione.tteolione.api.controller.user.request.DuplicateLoginIdReq;
import site.tteolione.tteolione.api.controller.user.request.SignUpReq;
import site.tteolione.tteolione.api.service.user.AuthService;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;
import site.tteolione.tteolione.config.exception.Code;
import site.tteolione.tteolione.docs.RestDocsSupport;
import site.tteolione.tteolione.domain.user.User;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;

public class AuthControllerDocsTest extends RestDocsSupport {

    private final AuthService authService = Mockito.mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
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
                "signUpRequest.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(request).getBytes()
        );

        // when
        BDDMockito.when(authService.signUpUser(Mockito.any(SignUpServiceReq.class), Mockito.any(MultipartFile.class)))
                .thenReturn(Mockito.mock(User.class));

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v2/auth/signup")
                        .file(profile)
                        .file(signUpRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("auth-signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParts(
                                partWithName("signUpRequest").description("회원가입 요청 정보"),
                                partWithName("profile").description("프로필 이미지 파일")
                        ),
                        requestPartFields("signUpRequest",
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("로그인 아이디"),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("username").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("닉네임"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN)
                                        .description("성공유무"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드값"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.STRING)
                                        .description("데이터"))
                ));
    }
}
