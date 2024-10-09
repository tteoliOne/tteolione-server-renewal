package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetServicePasswordReq {

    private String username;

    private String email;

    private String loginId;

    private String password;

    @Builder
    public ResetServicePasswordReq(String username, String email, String loginId, String password) {
        this.username = username;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
    }

}
