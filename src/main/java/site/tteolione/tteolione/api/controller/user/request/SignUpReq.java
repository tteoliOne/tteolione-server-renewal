package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;

@Getter
@NoArgsConstructor
public class SignUpReq {

    @NotNull(message = "회원님의 로그인Id를 적어주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])[a-z0-9]{6,20}$",
            message = "id는 소문자 하나이상있어야하고, 6자~20자여야합니다."
    )
    private String loginId;

    @NotBlank(message = "회원님의 이메일를 적어주세요.")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @NotBlank(message = "회원님의 이름을 적어주세요.")
    private String username;

    @NotBlank(message = "회원님의 닉네임을 적어주세요.")
    private String nickname;

    @NotBlank(message = "회원의 비밀번호를 적어주세요.")
    @Pattern(
            regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "8~16 characters consisting of letters(A-Z, a-z), numbers, or special characters."
    )
    private String password;

    @Builder
    public SignUpReq(String loginId, String email, String username, String nickname, String password) {
        this.loginId = loginId;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
    }

    public SignUpServiceReq toServiceRequest() {
        return SignUpServiceReq.builder()
                .loginId(loginId)
                .email(email)
                .username(username)
                .nickname(nickname)
                .password(password)
                .build();
    }
}
