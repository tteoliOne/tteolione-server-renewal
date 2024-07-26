package site.tteolione.tteolione.api.controller.email.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailAuthCodeReq {

    @NotBlank(message = "회원님의 이메일를 적어주세요.")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;


    @Size(min = 7, max = 7, message = "인증코드는 7자리입니다.")
    private String code;

    @Builder
    public EmailAuthCodeReq(String email, String code) {
        this.email = email;
        this.code = code;
    }
}
