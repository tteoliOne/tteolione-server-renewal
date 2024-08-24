package site.tteolione.tteolione.api.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.tteolione.tteolione.api.service.user.request.OAuth2KakaoServiceReq;
import site.tteolione.tteolione.api.service.user.response.LoginRes;
import site.tteolione.tteolione.client.oauth2.kakao.KakaoAuthClient;
import site.tteolione.tteolione.client.oauth2.kakao.response.KakaoUserInfoRes;
import site.tteolione.tteolione.client.s3.S3ImageService;
import site.tteolione.tteolione.common.config.exception.Code;
import site.tteolione.tteolione.common.config.exception.GeneralException;
import site.tteolione.tteolione.common.config.jwt.TokenInfoRes;
import site.tteolione.tteolione.common.config.jwt.TokenProvider;
import site.tteolione.tteolione.common.config.redis.RedisUtil;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.domain.user.User;
import site.tteolione.tteolione.domain.user.UserRepository;
import site.tteolione.tteolione.domain.user.constants.EAuthority;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2Service {

    private final UserRepository userRepository;
    private final KakaoAuthClient kakaoAuthClient;
    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final S3ImageService s3ImageService;

    @Transactional
    public LoginRes validateKakaoAccessToken(OAuth2KakaoServiceReq request) {

        // Kakao 사용자 정보 가져오기
        KakaoUserInfoRes kakaoInfoRes = kakaoAuthClient.getUserInfo("Bearer " + request.accessToken());
        HashMap<String, Object> userInfo = extractUserInfo(kakaoInfoRes);

        // 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmailAndLoginType(userInfo.get("email").toString(), ELoginType.eKakao);

        //사용자가 존재하지 않을 경우
        if (optionalUser.isEmpty()) {
            return LoginRes.fromKakao(null, null, null);
        }

        User user = optionalUser.get();

        // 탈퇴 유저 확인
        validateWithdrawUser(user);

        // FCM 토큰 업데이트
        user.changeTargetToken(request.targetToken());

        // 사용자 인증 정보 생성
        SecurityUserDto userDto = createSecurityUserDto(user);

        // 인증 설정
        Authentication auth = getAuthentication(userDto);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 토큰 생성 및 저장
        TokenInfoRes tokenInfoRes = createAndStoreToken(user);
        return LoginRes.fromKakao(tokenInfoRes, userInfo, user);
    }

    public LoginRes signUpKakao(MultipartFile profile, OAuth2KakaoServiceReq request) {

        // Kakao 사용자 정보 가져오기
        KakaoUserInfoRes kakaoInfoRes = kakaoAuthClient.getUserInfo("Bearer " + request.accessToken());
        HashMap<String, Object> userInfo = extractUserInfo(kakaoInfoRes);

        // 사용자 조회
        Optional<User> optionalUser = userRepository.findByEmailAndLoginType(userInfo.get("email").toString(), ELoginType.eKakao);

        //사용자가 존재하지 않을 경우
        if (optionalUser.isPresent()) {
            throw new GeneralException(Code.EXISTS_USER);
        }

        //이미지 업로드
        String userProfile = s3ImageService.upload(profile);

        //랜덤 닉네임 생성
        String randomNickname = createNickname();

        User user = request.toEntity(userInfo, userProfile, request.targetToken(), randomNickname);
        userRepository.save(user);

        // 사용자 인증 정보 생성
        SecurityUserDto userDto = createSecurityUserDto(user);

        // 인증 설정
        Authentication auth = getAuthentication(userDto);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 토큰 생성 및 저장
        TokenInfoRes tokenInfoRes = createAndStoreToken(user);
        return LoginRes.fromKakao(tokenInfoRes, userInfo, user);
    }

    private String createNickname() {
        //랜덤 닉네임 생성
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        while (userRepository.existsByNickname("user_" + randomNumber)) {
            randomNumber = random.nextInt(9000) + 1000;
        }
        return "user_" + randomNumber;
    }

    private SecurityUserDto createSecurityUserDto(User user) {
        return SecurityUserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .build();
    }

    private void validateWithdrawUser(User user) {
        //탈퇴 유저(2주동안 유지)
        if (user.getUserRole().equals(EAuthority.ROLE_WITHDRAW_USER)) {
            throw new GeneralException(Code.WITH_DRAW_USER);
        }
    }

    private HashMap<String, Object> extractUserInfo(KakaoUserInfoRes kakaoRes) {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("kakaoUserId", kakaoRes.id());
        userInfo.put("nickname", kakaoRes.properties().nickname());
        userInfo.put("email", kakaoRes.kakaoAccount().email());
        return userInfo;
    }

    private TokenInfoRes createAndStoreToken(User user) {
        TokenInfoRes tokenInfoRes = tokenProvider.createToken(user.getEmail(), user.getUserRole().name());

        redisUtil.setDataExpireMillis(
                "RT:" + user.getEmail(),
                tokenInfoRes.getRefreshToken(),
                tokenInfoRes.getRefreshTokenExpirationTime()
        );
        return tokenInfoRes;
    }

    public Authentication getAuthentication(SecurityUserDto userDto) {
        return new UsernamePasswordAuthenticationToken(userDto, "",
                List.of(new SimpleGrantedAuthority(userDto.getUserRole().name())));
    }

}
