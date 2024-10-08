package site.tteolione.tteolione.api.service.email;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.client.email.EmailSendClient;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.domain.mail.EmailAuth;
import site.tteolione.tteolione.domain.mail.EmailAuthRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final EmailSendClient emailSendClient;
    private final EmailAuthRepository emailAuthRepository;
    private final RedisUtil redisUtil;

    // 메일 보내기
    public boolean sendEmail(String toEmail) throws MessagingException {
        if (redisUtil.existData("code:"+toEmail)) {
            redisUtil.deleteData("code:"+toEmail);
        }
        return emailSendClient.sendEmail(toEmail);
    }

    // 코드 검증
    @Transactional
    public boolean verifyEmailCode(String email, String code, String codeFoundByEmail) {
        log.info("Redis 인증코드 값 = {}", codeFoundByEmail);

        if (codeFoundByEmail == null) {
            throw new GeneralException(Code.VALIDATION_AUTHCODE);
        }
        boolean verifySuccess = codeFoundByEmail.equals(code);
        if (verifySuccess) {
            emailAuthRepository.save(EmailAuth.createEmailAuth(email));
            redisUtil.deleteData("code:"+email);
            return true;
        } else {
            throw new GeneralException(Code.NOT_EXISTS_AUTHCODE);
        }
    }

}
