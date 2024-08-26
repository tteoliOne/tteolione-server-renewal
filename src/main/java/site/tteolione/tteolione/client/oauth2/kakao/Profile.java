package site.tteolione.tteolione.client.oauth2.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record Profile(
        String nickname,
        @JsonProperty("profile_image_url")
        String profileImageUrl
) {
    @Builder
    public Profile{

    }
}
