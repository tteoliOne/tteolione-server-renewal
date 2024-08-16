package site.tteolione.tteolione.api.service.email;

import jakarta.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import site.tteolione.tteolione.client.email.EmailSendClient;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.domain.mail.EmailAuth;
import site.tteolione.tteolione.domain.mail.EmailAuthRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private EmailSendClient emailSendClient;

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @InjectMocks
    private EmailService emailService;

    @AfterEach
    void tearDown() {
        emailAuthRepository.deleteAllInBatch();
    }

    @DisplayName("메일 전송 테스트")
    @Test
    void sendEmail() throws MessagingException {
        // given
        BDDMockito.given(emailSendClient.sendEmail(ArgumentMatchers.anyString()))
                .willReturn(true);

        // when
        boolean result = emailService.sendEmail("test123@naver.com");

        // then
        Assertions.assertThat(result).isEqualTo(true);
    }

    @DisplayName("이메일 인증코드 검증 성공 테스트")
    @Test
    void verifyEmailCodeSuccess() {
        // given
        String email = "test@example.com";
        String code = "test1234";
        String codeFoundByEmail = "test1234";

        // when
        emailService.verifyEmailCode(email, code, codeFoundByEmail);

        // then
        Mockito.verify(emailAuthRepository, Mockito.times(1)).save(ArgumentMatchers.any(EmailAuth.class));
    }

    @DisplayName("이메일 인증코드 검증 실패 테스트 -> 유효시간 5분 지났을 때")
    @Test
    void verifyEmailCodePass5Minutes() {
        // given
        String email = "test@example.com";
        String code = "test1234";
        String codeFoundByEmail = null;

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            emailService.verifyEmailCode(email, code, codeFoundByEmail);
        });


        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.VALIDATION_AUTHCODE);
    }

    @DisplayName("이메일 인증코드 검증 실패 테스트 -> 인증코드가 존재하지 않을 때")
    @Test
    void verifyEmailCodeNotMatch() {
        // given
        String email = "test@example.com";
        String code = "test1234";
        String codeFoundByEmail = "test123456";

        // when
        GeneralException exp = assertThrows(GeneralException.class, () -> {
            emailService.verifyEmailCode(email, code, codeFoundByEmail);
        });

        // then
        Assertions.assertThat(exp.getErrorCode()).isEqualTo(Code.NOT_EXISTS_AUTHCODE);
    }
}