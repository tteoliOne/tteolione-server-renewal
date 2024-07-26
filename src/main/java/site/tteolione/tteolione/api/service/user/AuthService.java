package site.tteolione.tteolione.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;
import site.tteolione.tteolione.config.exception.Code;
import site.tteolione.tteolione.config.exception.GeneralException;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String loginId) {
        return this.userRepository.findOneWithAuthoritiesByLoginId(loginId)
                .map(user -> createUser(loginId, user))
                .orElseThrow(() -> new UsernameNotFoundException(loginId + "유저 이름을 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        if (!user.isActivated()) throw new GeneralException("유저가 활성화되어 있지 않습니다.");
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getLoginId(),
                user.getPassword(),
                grantedAuthorities);
    }

    public void signUpUser(SignUpServiceReq signUpReq, MultipartFile profile) throws IOException {
        //이미 등록된 이메일 회원인지
        validateIsAlreadyEmailRegisteredUser(signUpReq.getEmail());
        //보낸 이메일이 인증되었는지 확인
//        EmailAuth findEmailAuth = validateEmailAuthEntity(signUpRequest.getEmail());
//        String saveProfile = s3Service.uploadFile(profile);
//
//        userRepository.save(User.toAppEntity(signUpRequest, passwordEncoder, findEmailAuth, saveProfile));
    }

    public void validateIsAlreadyEmailRegisteredUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if (findUser.isPresent()) {
            User user = findUser.get();
            if (user.isEmailAuthChecked()) {
                switch (user.getLoginType()) {
                    case eApp -> throw new GeneralException(Code.EXISTS_USER);
                    case eKakao -> throw new GeneralException(Code.EXISTS_KAKAO);
                    case eGoogle -> throw new GeneralException(Code.EXISTS_GOOGLE);
                    case eNaver -> throw new GeneralException(Code.EXISTS_NAVER);
                    case eApple -> throw new GeneralException(Code.EXISTS_APPLE);
                }
            }
        }
    }
}
