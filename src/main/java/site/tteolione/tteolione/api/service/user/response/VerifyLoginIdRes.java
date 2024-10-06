package site.tteolione.tteolione.api.service.user.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyLoginIdRes {

    private String loginId;

    @Builder
    public VerifyLoginIdRes(String loginId) {
        this.loginId = loginId;
    }

    public static VerifyLoginIdRes from(String loginId) {
        return VerifyLoginIdRes.builder()
                .loginId(loginId)
                .build();
    }
}
