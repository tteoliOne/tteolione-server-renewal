package site.tteolione.tteolione.api.controller.email.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailSendReq {

    @NotBlank(message = "회원님의 이메일를 적어주세요.")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Builder
    public EmailSendReq(String email) {
        this.email = email;
    }
}
