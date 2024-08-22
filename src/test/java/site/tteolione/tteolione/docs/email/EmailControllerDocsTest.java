package site.tteolione.tteolione.docs.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import site.tteolione.tteolione.api.controller.email.EmailController;
import site.tteolione.tteolione.api.controller.email.request.EmailAuthCodeReq;
import site.tteolione.tteolione.api.controller.email.request.EmailSendReq;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.docs.RestDocsSupport;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

public class EmailControllerDocsTest extends RestDocsSupport {

    private final EmailService emailService = Mockito.mock(EmailService.class);
    private final RedisUtil redisUtil = Mockito.mock(RedisUtil.class);

    @Override
    protected Object initController() {
        return new EmailController(emailService, redisUtil);
    }

    @DisplayName("회원가입시 이메일 전송하는 API")
    @Test
    void sendEmail() throws Exception {
        // given
        EmailSendReq request = EmailSendReq.builder()
                .email("test123@naver.com")
                .build();

        BDDMockito.given(emailService.sendEmail(Mockito.any(String.class)))
                .willReturn(true);

        // when
        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v2/email/send")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("email-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일")
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

    @DisplayName("회원가입시 이메일 인증코드 검증하는 API")
    @Test
    void verifyEmailAndCode() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .email("test123@naver.com")
                .code("1231234")
                .build();

        BDDMockito.given(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.any()))
                .willReturn(true);

        // when
        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v2/email/verify")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("email-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("이메일"),
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("인증코드")
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
