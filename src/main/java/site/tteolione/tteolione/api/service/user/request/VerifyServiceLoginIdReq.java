package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerifyServiceLoginIdReq {

    private String username;

    private String authCode;

    private String email;

    @Builder

    public VerifyServiceLoginIdReq(String username, String authCode, String email) {
        this.username = username;
        this.authCode = authCode;
        this.email = email;
    }
}
