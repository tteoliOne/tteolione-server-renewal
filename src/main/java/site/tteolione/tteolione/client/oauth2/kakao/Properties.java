package site.tteolione.tteolione.client.oauth2.kakao;

import lombok.Builder;

public record Properties(
        String nickname
) {
    @Builder
    public Properties {}
}
