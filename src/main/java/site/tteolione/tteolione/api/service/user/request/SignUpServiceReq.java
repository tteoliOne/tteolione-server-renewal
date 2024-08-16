package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.tteolione.tteolione.domain.mail.EmailAuth;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Collections;

@Getter
@NoArgsConstructor
public class SignUpServiceReq {

    private String loginId;

    private String email;

    private String username;

    private String nickname;

    private String password;

    @Builder
    public SignUpServiceReq(String loginId, String email, String username, String nickname, String password) {
        this.loginId = loginId;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }

    public User toEntity(PasswordEncoder passwordEncoder, String imageUrl) {
        return User.builder()
                .loginId(this.loginId)
                .email(this.email)
                .username(this.username)
                .nickname(this.nickname)
                .password(passwordEncoder.encode(this.password))
                .profile(imageUrl)
                .emailAuthChecked(true)
                .loginType(ELoginType.eApp)
                .activated(true)
                .userRole(EAuthority.ROLE_USER)
                .build();
    }
}
