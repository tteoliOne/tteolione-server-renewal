package site.tteolione.tteolione.api.controller.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.config.jwt.TokenInfoRes;
import site.tteolione.tteolione.domain.user.User;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {

    //기존회원일때
    private boolean existsUser;
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String nickname;

    // Apple 로그인 시에만 사용될 refresh token
    private String appleRefreshToken;

    public static LoginRes fromApp(User user, TokenInfoRes tokenInfoRes) {
        return LoginRes.builder()
                .existsUser(true)
                .userId(user.getUserId())
                .accessToken(tokenInfoRes.getAccessToken())
                .refreshToken(tokenInfoRes.getRefreshToken())
                .nickname(user.getNickname())
                .build();
    }

}
