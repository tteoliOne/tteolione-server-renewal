package site.tteolione.tteolione.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.tteolione.tteolione.api.service.user.request.ChangeNicknameServiceReq;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

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
}
