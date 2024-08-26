package site.tteolione.tteolione.api.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.controller.user.request.OAuth2KakaoReq;
import site.tteolione.tteolione.api.service.user.OAuth2Service;
import site.tteolione.tteolione.api.service.user.response.LoginRes;
import site.tteolione.tteolione.common.config.exception.BaseResponse;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/auth")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @PostMapping("/kakao")
    public BaseResponse<LoginRes> kakaoLogin(@Valid @RequestBody OAuth2KakaoReq request) {
        LoginRes response = oAuth2Service.validateKakaoAccessToken(request.toServiceRequest());
        return BaseResponse.of(response);
    }

    @PostMapping(path = "/kakao/profile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<LoginRes> kakaoSignup(
            @RequestPart(value = "profile") MultipartFile profile,
            @Valid @RequestPart(value = "request") OAuth2KakaoReq request
    ) {
        LoginRes response = oAuth2Service.signUpKakao(profile, request.toServiceRequest());
        return BaseResponse.of(response);
    }
}
