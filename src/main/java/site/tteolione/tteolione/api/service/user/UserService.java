package site.tteolione.tteolione.api.service.user;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.api.service.email.EmailService;
import site.tteolione.tteolione.api.service.user.request.*;
import site.tteolione.tteolione.api.service.user.response.VerifyLoginIdRes;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;

    public boolean duplicateLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new GeneralException(Code.EXISTS_LOGIN_ID);
        }

        return true;
    }

    public boolean existByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new GeneralException(Code.NOT_EXISTS_USER));
    }

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(() -> new GeneralException(Code.NOT_EXISTS_USER));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(Code.NOT_EXISTS_USER));
    }

    @Transactional
    public void changeNickname(SecurityUserDto userDto, ChangeNicknameServiceReq request) {
        Long userId = userDto.getUserId();
        User user = findById(userId);

        String newNickname = request.nickname();

        //로그인 회원의 기존 닉네임과 일치한지
        if (user.getNickname().equals(newNickname)) {
            throw new GeneralException(Code.EQUALS_NICKNAME);
        }

        //바꾸고자 하는 닉네임이 다른 회원들중에 존재하는지
        if (existByNickname(newNickname)) {
            throw new GeneralException(Code.EXIST_NICKNAME);
        }

        user.changeNickname(newNickname);
    }

    public String findLoginId(FindServiceLoginIdReq request) throws MessagingException {
        User findUser = userRepository.findByUsernameAndEmail(request.getUsername(), request.getEmail())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND_USER_INFO));

        switch (findUser.getLoginType()) {
            case eKakao -> throw new GeneralException(Code.FOUND_KAKAO_USER);
            case eGoogle -> throw new GeneralException(Code.FOUND_GOOGLE_USER);
            case eNaver -> throw new GeneralException(Code.FOUND_NAVER_USER);
            case eApple -> throw new GeneralException(Code.FOUND_APPLE_USER);
        }

        boolean result = emailService.sendEmail(request.getEmail());
        if (result) {
            return "이메일 인증코드 발송에 성공했습니다.";
        }
        return "이메일 인증코드 발송에 실패했습니다.";

    }

    public VerifyLoginIdRes verifyLoginId(VerifyServiceLoginIdReq request) {
        String codeFoundByEmail = redisUtil.getData("code:" + request.getEmail());
        boolean isVerify = emailService.verifyEmailCode(request.getEmail(), request.getAuthCode(), codeFoundByEmail);
        if (!isVerify) {
            throw new GeneralException(Code.VERIFY_EMAIL_CODE);
        }
        User findUser = userRepository.findByUsernameAndEmail(request.getUsername(), request.getEmail())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND_USER_INFO));
        return VerifyLoginIdRes.from(findUser.getLoginId());
    }

    public String findPassword(FindServicePasswordReq request) throws MessagingException {
        User findUser = userRepository.findByUsernameAndEmailAndLoginId(request.getUsername(), request.getEmail(), request.getLoginId())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND_USER_INFO));

        switch (findUser.getLoginType()) {
            case eKakao -> throw new GeneralException(Code.FOUND_KAKAO_USER);
            case eGoogle -> throw new GeneralException(Code.FOUND_GOOGLE_USER);
            case eNaver -> throw new GeneralException(Code.FOUND_NAVER_USER);
            case eApple -> throw new GeneralException(Code.FOUND_APPLE_USER);
        }

        boolean result = emailService.sendEmail(request.getEmail());
        if (result) {
            return "이메일 인증코드 발송에 성공했습니다.";
        }
        return "이메일 인증코드 발송에 실패했습니다.";
    }

    public String verifyPassword(VerifyServicePasswordReq request) {
        String codeFoundByEmail = redisUtil.getData("code:" + request.getEmail());
        boolean isVerify = emailService.verifyEmailCode(request.getEmail(), request.getAuthCode(), codeFoundByEmail);
        if (!isVerify) {
            throw new GeneralException(Code.VERIFY_EMAIL_CODE);
        }
        userRepository.findByUsernameAndEmailAndLoginId(request.getUsername(), request.getEmail(), request.getLoginId())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND_USER_INFO));
        return "비밀번호 이메일 검증 성공";
    }

    @Transactional
    public String resetPassword(ResetServicePasswordReq request) {
        User findUser = userRepository.findByUsernameAndEmailAndLoginId(request.getUsername(), request.getEmail(), request.getLoginId())
                .orElseThrow(() -> new GeneralException(Code.NOT_FOUND_USER_INFO));

        switch (findUser.getLoginType()) {
            case eKakao -> throw new GeneralException(Code.FOUND_KAKAO_USER);
            case eGoogle -> throw new GeneralException(Code.FOUND_GOOGLE_USER);
            case eNaver -> throw new GeneralException(Code.FOUND_NAVER_USER);
            case eApple -> throw new GeneralException(Code.FOUND_APPLE_USER);
        }

        if (passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
            throw new GeneralException(Code.MATCH_EXIST_PW);
        }

        findUser.changePassword(passwordEncoder.encode(request.getPassword()));

        return "비밀번호 재설정 성공";
    }
}
