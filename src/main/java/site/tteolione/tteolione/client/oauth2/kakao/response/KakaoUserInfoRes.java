package site.tteolione.tteolione.client.oauth2.kakao.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import site.tteolione.tteolione.client.oauth2.kakao.KakaoAccount;
import site.tteolione.tteolione.client.oauth2.kakao.Properties;

public record KakaoUserInfoRes(
        String id,
        Properties properties,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {

    @Builder
    public KakaoUserInfoRes {
    }
}