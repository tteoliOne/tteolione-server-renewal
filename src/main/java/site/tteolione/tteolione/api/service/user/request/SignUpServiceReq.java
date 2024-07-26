package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
