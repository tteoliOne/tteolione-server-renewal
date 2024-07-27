package site.tteolione.tteolione.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.controller.user.response.LoginRes;
import site.tteolione.tteolione.api.service.user.request.LoginServiceReq;
import site.tteolione.tteolione.api.service.user.request.SignUpServiceReq;
import site.tteolione.tteolione.client.s3.S3ImageService;
import site.tteolione.tteolione.config.exception.Code;
import site.tteolione.tteolione.config.exception.GeneralException;
import site.tteolione.tteolione.config.jwt.TokenInfoRes;
import site.tteolione.tteolione.config.jwt.TokenProvider;
import site.tteolione.tteolione.config.redis.RedisUtil;
import site.tteolione.tteolione.domain.mail.EmailAuth;
import site.tteolione.tteolione.domain.mail.EmailAuthRepository;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final EmailAuthRepository emailAuthRepository;
    private final UserRepository userRepository;
    private final S3ImageService s3ImageService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;

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

    /**
     *  회원가입 서비스 로직
     */
    @Transactional
    public User signUpUser(SignUpServiceReq request, MultipartFile profile)  {
        //이미 등록된 이메일 회원인지
        validateIsAlreadyEmailRegisteredUser(request.getEmail());
        //보낸 이메일이 인증되었는지 확인
        EmailAuth emailAuth = validateEmailAuth(request.getEmail());
        String saveProfile = s3ImageService.upload(profile);

        User saveUser = userRepository.save(request.toEntity(passwordEncoder, saveProfile));

        //회원가입이 끝난 후 인증된 메일 삭제
        emailAuthRepository.deleteByEmail(emailAuth.getEmail());
        return saveUser;
    }

    private EmailAuth validateEmailAuth(String email) {
        return emailAuthRepository.findByEmail(email)
                .orElseThrow(() -> new GeneralException(Code.VALIDATION_EMAIL));
    }

    private void validateIsAlreadyEmailRegisteredUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isEmailAuthChecked()) {
                switch (user.getLoginType()) {
                    case eApp -> throw new GeneralException(Code.EXISTS_USER);
                    case eKakao -> throw new GeneralException(Code.EXISTS_KAKAO);
                    case eGoogle -> throw new GeneralException(Code.EXISTS_GOOGLE);
                    case eNaver -> throw new GeneralException(Code.EXISTS_NAVER);
                    case eApple -> throw new GeneralException(Code.EXISTS_APPLE);
                }
            }
        });
    }

    /**
     * 로그인 서비스 로직
     */
    public LoginRes loginUser(LoginServiceReq request) {
        String loginId = request.getLoginId();
        User user = userRepository.findOneWithAuthoritiesByLoginId(loginId)
                .orElseThrow(() -> new GeneralException(Code.NOT_EXISTS_LOGIN_ID_PW));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GeneralException(Code.NOT_EXISTS_LOGIN_ID_PW);
        }

        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority.getAuthority().equals(EAuthority.eWithdrawalUser.getValue())) {
                throw new GeneralException(Code.WITH_DRAW_USER);
            }
        }

        user.changeTargetToken(request.getTargetToken());
        TokenInfoRes tokenInfoRes = createAndStoreToken(loginId);

        return LoginRes.fromApp(user, tokenInfoRes);
    }

    private TokenInfoRes createAndStoreToken(String loginId) {
        UserDetails userDetails = loadUserByUsername(loginId);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        TokenInfoRes tokenInfoRes = tokenProvider.createToken(authenticationToken);
        redisUtil.setDataExpire(
                "RT:" + authenticationToken.getName(),
                tokenInfoRes.getRefreshToken(),
                tokenInfoRes.getRefreshTokenExpirationTime()
        );
        return tokenInfoRes;
    }
}
