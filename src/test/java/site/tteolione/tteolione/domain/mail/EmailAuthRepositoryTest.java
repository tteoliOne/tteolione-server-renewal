package site.tteolione.tteolione.domain.mail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.IntegrationTestSupport;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class EmailAuthRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private EmailAuthRepository emailAuthRepository;


    @AfterEach
    void tearDown() {
        emailAuthRepository.deleteAllInBatch();
    }

    @DisplayName("찾고싶은 이메일 존재할때")
    @Test
    void findByEmail() {
        // given
        String findEmail = "test123@naver.com";
        EmailAuth emailAuth1 = EmailAuth.createEmailAuth("test123@naver.com");
        EmailAuth saveEmailAuth = emailAuthRepository.save(emailAuth1);

        // when
        Optional<EmailAuth> findEmailAuth_ = emailAuthRepository.findByEmail(findEmail);
        EmailAuth findEmailAuth = findEmailAuth_.get();

        // then
        Assertions.assertThat(findEmailAuth).isEqualTo(saveEmailAuth);
    }

    @DisplayName("찾고싶은 이메일 존재하지 않을때")
    @Test
    void findByEmailNull() {
        // given
        String findEmail = "find123@naver.com";
        EmailAuth emailAuth1 = EmailAuth.createEmailAuth("test123@naver.com");
        EmailAuth emailAuth2 = EmailAuth.createEmailAuth("test1234@naver.com");
        EmailAuth emailAuth3 = EmailAuth.createEmailAuth("test12345@naver.com");
        emailAuthRepository.saveAll(List.of(emailAuth1, emailAuth2, emailAuth3));

        // when
        Optional<EmailAuth> findEmailAuth_ = emailAuthRepository.findByEmail(findEmail);

        // then
        Assertions.assertThat(findEmailAuth_).isEmpty();
    }

    @DisplayName("원하는 이메일 전부 삭제")
    @Test
    void deleteByEmail() {
        // given

        // when

        // then
    }
}