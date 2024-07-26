package site.tteolione.tteolione.api.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.controller.user.request.SignUpReq;
import site.tteolione.tteolione.api.service.user.AuthService;
import site.tteolione.tteolione.config.exception.BaseResponse;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<String> signupUser(@Valid @RequestPart(value = "signUpRequest") SignUpReq request,
                                           @RequestPart(value = "profile") MultipartFile profile) throws IOException {
        authService.signUpUser(request.toServiceRequest(), profile);
        return BaseResponse.of("회원가입 성공입니다.");
    }
}
