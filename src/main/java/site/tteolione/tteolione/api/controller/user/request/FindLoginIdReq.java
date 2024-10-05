package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.FindServiceLoginIdReq;

@Data
@NoArgsConstructor
public class FindLoginIdReq {

    @NotBlank(message = "회원님의 이름을 적어주세요.")
    private String username;

    @NotBlank(message = "회원님의 이메일을 적어주세요.")
    @Email(message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @Builder
    public FindLoginIdReq(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public FindServiceLoginIdReq toServiceRequest() {
        return FindServiceLoginIdReq.builder()
                .username(username)
                .email(email)
                .build();
    }
}
