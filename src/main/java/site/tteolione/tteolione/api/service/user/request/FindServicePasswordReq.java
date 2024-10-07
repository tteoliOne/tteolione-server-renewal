package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindServicePasswordReq {

    private String username;

    private String email;

    private String loginId;

    @Builder
    public FindServicePasswordReq(String username, String email, String loginId) {
        this.username = username;
        this.email = email;
        this.loginId = loginId;
    }

}
