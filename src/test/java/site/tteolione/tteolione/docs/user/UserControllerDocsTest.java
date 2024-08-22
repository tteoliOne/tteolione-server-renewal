package site.tteolione.tteolione.docs.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import site.tteolione.tteolione.GenerateMockToken;
import site.tteolione.tteolione.WithMockCustomAccount;
import site.tteolione.tteolione.api.controller.email.request.EmailSendReq;
import site.tteolione.tteolione.api.controller.user.UserController;
import site.tteolione.tteolione.api.controller.user.request.ChangeNicknameReq;
import site.tteolione.tteolione.api.controller.user.request.DupNicknameReq;
import site.tteolione.tteolione.api.controller.user.request.DuplicateLoginIdReq;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.common.util.SecurityUtils;
import site.tteolione.tteolione.docs.RestDocsSupport;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = Mockito.mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
    }

    @DisplayName("회원가입시 로그인 아이디 중복체크 API")
    @Test
    void duplicateLoginId() throws Exception {
        // given
        DuplicateLoginIdReq request = DuplicateLoginIdReq.builder()
                .loginId("test123")
                .build();

        BDDMockito.given(userService.duplicateLoginId(Mockito.anyString()))
                .willReturn(true);

        // when
        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v2/users/check/login-id")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("duplicate-loginId",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("로그인 아이디")
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

    @DisplayName("닉네임 중복체크 API")
    @Test
    void duplicateNickname() throws Exception {
        // given
        DupNicknameReq request = DupNicknameReq.builder()
                .nickname("test12")
                .build();

        BDDMockito.given(userService.existByNickname(Mockito.anyString()))
                .willReturn(false);

        // when
        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v2/users/check/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("duplicate-nickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("닉네임")
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

    @DisplayName("닉네임 변경 API")
    @Test
    @WithMockCustomAccount
    void changeNickname() throws Exception {
        // given
        String newNickname = "새로운닉네임";

        SecurityUserDto userDto = SecurityUtils.getUser();

        ChangeNicknameReq request = ChangeNicknameReq.builder()
                .nickname(newNickname)
                .build();

        BDDMockito.doNothing().when(userService).changeNickname(userDto, request.toServiceRequest());

        // when
        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/api/v2/users/nickname")
                                .content(objectMapper.writeValueAsString(request))
                                .headers(GenerateMockToken.getToken())
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("change-nickname",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("accessToken")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING)
                                        .description("닉네임(2글자이상 6글자이하)")
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
