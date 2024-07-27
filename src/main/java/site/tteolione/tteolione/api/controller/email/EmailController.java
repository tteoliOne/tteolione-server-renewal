package site.tteolione.tteolione.api.controller.email;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.tteolione.tteolione.api.controller.email.request.EmailAuthCodeReq;
import site.tteolione.tteolione.api.controller.email.request.EmailSendReq;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.config.exception.BaseResponse;
import site.tteolione.tteolione.config.redis.RedisUtil;

import java.security.NoSuchAlgorithmException;


//TODO https://reeeemind.tistory.com/151
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/email")
public class EmailController {

    private final EmailService emailService;
    private final RedisUtil redisUtil;

    @PostMapping("/send")
    public BaseResponse<String> sendEmail(@Valid @RequestBody EmailSendReq request) throws MessagingException {
        boolean result = emailService.sendEmail(request.getEmail());
        if (result) {
            return BaseResponse.of("이메일 인증코드 발송에 성공했습니다.");
        }
        return BaseResponse.of("이메일 인증코드 발송에 실패했습니다.");
    }

    @PostMapping("/verify")
    public BaseResponse<String> verifyEmailAndCode(@Valid @RequestBody EmailAuthCodeReq request) throws NoSuchAlgorithmException {
        String codeFoundByEmail = redisUtil.getData("code:"+request.getEmail());
        emailService.verifyEmailCode(request.getEmail(), request.getCode(), codeFoundByEmail);
        return BaseResponse.of("이메일 인증 성공했습니다.");
    }
}