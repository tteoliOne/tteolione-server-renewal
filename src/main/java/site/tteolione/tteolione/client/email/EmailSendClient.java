package site.tteolione.tteolione.client.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import site.tteolione.tteolione.config.redis.RedisUtil;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class EmailSendClient {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String username;

    private String createCode() {
        Random random = new Random();
        String authCode = "";

        for (int i = 0; i < 3; i++) {
            int index = random.nextInt(25) + 65;
            authCode += (char) index;
        }

        int numIndex = random.nextInt(9000) + 1000;
        authCode += numIndex;

        return authCode;
    }

    private String setContext(String code) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("code", code);


        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("mail", context);
    }


    // 메일 반환
    private MimeMessage createEmailForm(String email) throws MessagingException {

        String authCode = createCode();

        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("[떠리원] 요청하신 인증코드는 " + authCode + "입니다.");
        message.setFrom(username + "@naver.com");
        message.setText(setContext(authCode), "utf-8", "html");

        redisUtil.setDataExpire("code:"+email, authCode, 60 * 5L);

        return message;
    }

    // 메일 보내기
    public boolean sendEmail(String toEmail) throws MessagingException {

        MimeMessage emailForm = createEmailForm(toEmail);

        mailSender.send(emailForm);

        return true;
    }

}
