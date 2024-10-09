package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.ResetServicePasswordReq;

@Data
@NoArgsConstructor
public class ResetPasswordReq {

    @NotBlank(message = "회원님의 이름을 적어주세요.")
    private String username;

    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @NotBlank(message = "회원님의 로그인Id를 적어주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])[a-z0-9]{6,20}$",
            message = "id는 소문자 하나이상있어야하고, 6자~20자여야합니다."
    )
    private String loginId;

    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "8~16 characters consisting of letters(A-Z, a-z), numbers, or special characters."
    )
    private String password;

    @Builder
    public ResetPasswordReq(String username, String email, String loginId, String password) {
        this.username = username;
        this.email = email;
        this.loginId = loginId;
        this.password = password;
    }

    public ResetServicePasswordReq toServiceRequest() {
        return ResetServicePasswordReq.builder()
                .username(username)
                .email(email)
                .loginId(loginId)
                .password(password)
                .build();
    }
}
