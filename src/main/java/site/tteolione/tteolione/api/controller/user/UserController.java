package site.tteolione.tteolione.api.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.tteolione.tteolione.api.controller.user.request.DuplicateLoginIdReq;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.config.exception.BaseResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 아이디 중복 확인
     */
    @PostMapping("/check/login-id")
    public BaseResponse<String> duplicateLoginId(@Valid @RequestBody DuplicateLoginIdReq request) {
        userService.duplicateLoginId(request.getLoginId());
        return BaseResponse.of("사용가능한 아이디입니다.");
    }

}
