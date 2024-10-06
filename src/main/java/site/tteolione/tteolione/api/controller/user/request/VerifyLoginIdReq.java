package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.VerifyServiceLoginIdReq;

@Data
@NoArgsConstructor
public class VerifyLoginIdReq {

    @NotBlank(message = "회원님의 이름을 적어주세요.")
    private String username;

    @Size(min = 7, max = 7, message = "인증코드는 7자리입니다.")
    private String authCode;

    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Builder
    public VerifyLoginIdReq(String username, String authCode, String email) {
        this.username = username;
        this.authCode = authCode;
        this.email = email;
    }

    public VerifyServiceLoginIdReq toServiceRequest() {
        return VerifyServiceLoginIdReq.builder()
                .username(username)
                .authCode(authCode)
                .email(email)
                .build();
    }
}
