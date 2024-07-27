package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginServiceReq {
    private String loginId;
    private String password;
    private String targetToken;

    @Builder
    public LoginServiceReq(String loginId, String password, String targetToken) {
        this.loginId = loginId;
        this.password = password;
        this.targetToken = targetToken;
    }
}
