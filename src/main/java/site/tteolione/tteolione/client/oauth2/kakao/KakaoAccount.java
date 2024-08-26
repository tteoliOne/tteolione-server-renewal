package site.tteolione.tteolione.client.oauth2.kakao;

import lombok.Builder;

public record KakaoAccount(
        String email,
        Profile profile
) {

    @Builder
    public KakaoAccount {
    }
}

