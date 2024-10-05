package site.tteolione.tteolione.api.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import site.tteolione.tteolione.api.controller.user.request.ChangeNicknameReq;
import site.tteolione.tteolione.api.controller.user.request.DupNicknameReq;
import site.tteolione.tteolione.api.controller.user.request.DuplicateLoginIdReq;
import site.tteolione.tteolione.api.controller.user.request.FindLoginIdReq;
import site.tteolione.tteolione.api.service.user.UserService;
import site.tteolione.tteolione.common.config.exception.BaseResponse;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.util.CurrentUser;
import site.tteolione.tteolione.common.util.SecurityUserDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
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

    /**
     * 닉네임 중복 확인
     */
    @PostMapping("/check/nickname")
    public BaseResponse<String> duplicateNickname(@Valid @RequestBody DupNicknameReq request) {
        if (userService.existByNickname(request.getNickname())) {
            throw new GeneralException(Code.EXIST_NICKNAME);
        }
        return BaseResponse.of("사용가능한 닉네임입니다.");
    }

    /**
     * 닉네임 변경
     */
    @PatchMapping("/nickname")
    public BaseResponse<String> changeNickname(
            @CurrentUser SecurityUserDto userDto,
            @Valid @RequestBody ChangeNicknameReq request
    ) {
        userService.changeNickname(userDto, request.toServiceRequest());
        return BaseResponse.of("정상적으로 닉네임이 변경되었습니다.");
    }

    @PostMapping("/find/login-id")
    public BaseResponse<String> findLoginId(@Valid @RequestBody FindLoginIdReq request) throws MessagingException {
        return BaseResponse.of(userService.findLoginId(request.toServiceRequest()));
    }
}
