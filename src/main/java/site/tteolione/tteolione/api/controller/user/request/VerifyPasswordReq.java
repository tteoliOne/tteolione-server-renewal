package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.VerifyServicePasswordReq;

@Data
@NoArgsConstructor
public class VerifyPasswordReq {

    @NotBlank(message = "회원님의 이름을 적어주세요.")
    private String username;

    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])[a-z0-9]{6,20}$",
            message = "id는 소문자 하나이상있어야하고, 6자~20자여야합니다."
    )
    private String loginId;

    @Size(min = 7, max = 7, message = "인증코드는 7자리입니다.")
    private String authCode;

    @Builder
    public VerifyPasswordReq(String username, String email, String loginId, String authCode) {
        this.username = username;
        this.email = email;
        this.loginId = loginId;
        this.authCode = authCode;
    }

    public VerifyServicePasswordReq toServiceRequest() {
        return VerifyServicePasswordReq.builder()
                .loginId(loginId)
                .authCode(authCode)
                .email(email)
                .username(username)
                .build();
    }
}
