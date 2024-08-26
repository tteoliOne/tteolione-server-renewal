package site.tteolione.tteolione.api.controller.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.api.service.user.request.OAuth2KakaoServiceReq;

@Getter
@NoArgsConstructor
public class OAuth2KakaoReq {

    @NotBlank(message = "카카오의 accessToken을 입력해 주세요.")
    private String accessToken;

    private String targetToken;

    @Builder
    public OAuth2KakaoReq(String accessToken, String targetToken) {
        this.accessToken = accessToken;
        this.targetToken = targetToken;
    }

    public OAuth2KakaoServiceReq toServiceRequest() {
        return OAuth2KakaoServiceReq.builder()
                .accessToken(accessToken)
                .targetToken(targetToken)
                .build();
    }
}
