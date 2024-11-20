package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.ChangeServicePasswordReq;

@Data
@NoArgsConstructor
public class ChangePasswordReq {

    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "8~16 characters consisting of letters(A-Z, a-z), numbers, or special characters."
    )
    private String password;

    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "8~16 characters consisting of letters(A-Z, a-z), numbers, or special characters."
    )
    private String newPassword;

    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "8~16 characters consisting of letters(A-Z, a-z), numbers, or special characters."
    )
    private String newPasswordConfirm;

    @Builder
    public ChangePasswordReq(String password, String newPassword, String newPasswordConfirm) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public ChangeServicePasswordReq toServiceRequest() {
        return ChangeServicePasswordReq.builder()
                .password(password)
                .newPassword(password)
                .newPasswordConfirm(newPasswordConfirm)
                .build();
    }
}
