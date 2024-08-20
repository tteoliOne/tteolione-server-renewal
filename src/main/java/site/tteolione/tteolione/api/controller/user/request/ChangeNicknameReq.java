package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.ChangeNicknameServiceReq;

@Data
@NoArgsConstructor
public class ChangeNicknameReq {

    @Size(min = 2, max = 6, message = "닉네임은 2글자 이상 6글자 이하이여야 합니다.")
    private String nickname;

    @Builder
    public ChangeNicknameReq(String nickname) {
        this.nickname = nickname;
    }

    public ChangeNicknameServiceReq toServiceRequest() {
        return ChangeNicknameServiceReq.builder()
                .nickname(nickname)
                .build();
    }
}
