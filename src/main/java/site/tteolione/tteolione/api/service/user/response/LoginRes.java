package site.tteolione.tteolione.api.service.user.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.tteolione.tteolione.common.config.jwt.TokenInfoRes;
import site.tteolione.tteolione.domain.user.User;

import java.util.HashMap;

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

    public static LoginRes fromKakao(TokenInfoRes tokenInfoRes, User user) {
        return LoginRes.builder()
                .existsUser(user != null)
                .userId(user == null ? null : user.getUserId())
                .accessToken(tokenInfoRes == null ? null : tokenInfoRes.getAccessToken())
                .refreshToken(tokenInfoRes == null ? null : tokenInfoRes.getRefreshToken())
                .nickname(user == null ? null : user.getNickname())
                .build();
    }

}
