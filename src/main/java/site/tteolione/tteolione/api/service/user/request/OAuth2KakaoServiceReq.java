package site.tteolione.tteolione.api.service.user.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.Collections;
import java.util.HashMap;

public record OAuth2KakaoServiceReq(
        String accessToken,
        String targetToken
) {

    @Builder
    public OAuth2KakaoServiceReq{
    }

    public User toEntity(HashMap<String, Object> userInfo, String userProfile, String targetToken, String randomNickname) {
        return User.builder()
                .loginId(userInfo.get("email").toString())
                .username(userInfo.get("nickname").toString())
                .nickname(randomNickname)
                .profile(userProfile)
                .email(userInfo.get("email").toString())
                .targetToken(targetToken)
                .loginType(ELoginType.eKakao)
                .providerId(userInfo.get("kakaoUserId").toString())
                .emailAuthChecked(true)
                .activated(true)
                .userRole(EAuthority.ROLE_USER)
                .build();
    }

}
