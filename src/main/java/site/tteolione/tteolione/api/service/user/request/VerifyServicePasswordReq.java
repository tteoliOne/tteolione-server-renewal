package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyServicePasswordReq {

    private String username;

    private String email;

    private String loginId;

    private String authCode;

    @Builder
    public VerifyServicePasswordReq(String username, String authCode, String email, String loginId) {
        this.username = username;
        this.authCode = authCode;
        this.email = email;
        this.loginId = loginId;
    }
}
