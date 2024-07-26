package site.tteolione.tteolione.api.controller.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import site.tteolione.tteolione.ControllerTestSupport;
import site.tteolione.tteolione.api.controller.email.request.EmailAuthCodeReq;
import site.tteolione.tteolione.api.controller.email.request.EmailSendReq;
import site.tteolione.tteolione.config.exception.Code;

@WithMockUser
class EmailControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입하기 전 이메일을 전송해야한다.")
    @Test
    void sendEmail() throws Exception {
        // given
        EmailSendReq request = EmailSendReq.builder()
                .email("test123@naver.com")
                .build();

        Mockito.when(emailService.sendEmail(Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/send")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("이메일 인증코드 발송에 성공했습니다."));
    }

    @DisplayName("이메일을 전송할 때 이메일은 필수 값이다. -> 빈문자열")
    @Test
    void sendEmailEmpty() throws Exception {
        // given
        EmailSendReq request = EmailSendReq.builder()
                .email("")
                .build();

        Mockito.when(emailService.sendEmail(Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/send")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("이메일을 전송할 때 이메일은 필수 값이다. -> null")
    @Test
    void sendEmailWithoutEmail() throws Exception {
        // given
        EmailSendReq request = EmailSendReq.builder()
                .build();

        Mockito.when(emailService.sendEmail(Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/send")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("이메일을 전송할 때 이메일 형식이 맞지 않을 때")
    @Test
    void validateEmailType() throws Exception {
        // given
        EmailSendReq request = EmailSendReq.builder()
                .email("test123")
                .build();

        Mockito.when(emailService.sendEmail(Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/send")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("이메일 검증 테스트")
    @Test
    void verifyEmailAndCode() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .email("test123@naver.com")
                .code("1231234")
                .build();

        Mockito.when(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/verify")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.OK.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Ok"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("이메일 인증 성공했습니다."));
    }

    @DisplayName("이메일을 검증할 때 이메일은 필수 값이다. -> 빈문자열")
    @Test
    void verifyEmailAndCodeEmptyEmail() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .email("")
                .code("1231234")
                .build();

        Mockito.when(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/verify")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("이메일을 검증할 때 이메일은 필수 값이다. -> null")
    @Test
    void verifyEmailAndCodeWithoutEmail() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .code("1231234")
                .build();

        Mockito.when(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/verify")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("회원님의 이메일를 적어주세요."));
    }

    @DisplayName("이메일을 검증할 때 이메일 형식이 맞지 않을 때")
    @Test
    void validateEmailType2() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .email("test123")
                .code("1231231")
                .build();

        Mockito.when(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/verify")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이메일 형식이 맞지 않습니다."));
    }

    @DisplayName("이메일을 검증할 때 인증코드가 7자리가 아닐 때")
    @Test
    void verifyEmailAndCodeSizeNot7() throws Exception {
        // given
        EmailAuthCodeReq request = EmailAuthCodeReq.builder()
                .email("test123@naver.com")
                .code("12312")
                .build();

        Mockito.when(emailService.verifyEmailCode(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        // when
        // then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v2/email/verify")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(Code.VALIDATION_ERROR.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("인증코드는 7자리입니다."));
    }


}