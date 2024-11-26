package site.tteolione.tteolione.api.service.user.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeServicePasswordReq {

    private String password;

    private String newPassword;

    private String newPasswordConfirm;

    @Builder
    public ChangeServicePasswordReq(String password, String newPassword, String newPasswordConfirm) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
