package site.tteolione.tteolione.client.oauth2.kakao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import site.tteolione.tteolione.client.oauth2.kakao.request.KakaoRevokeReq;
import site.tteolione.tteolione.client.oauth2.kakao.response.KakaoUserInfoRes;

//유저 정보 가져오기
@FeignClient(name = "kakaoClient", url = "https://kapi.kakao.com")
public interface KakaoAuthClient {

    @GetMapping(value = "/v2/user/me", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    KakaoUserInfoRes getUserInfo(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION)
            String Authorization
    );

    @PostMapping(value = "/v1/user/unlink", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void revoke(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION)
            String Authorization,
            @RequestBody
            KakaoRevokeReq kakaoRevokeReq
    );

}