package site.tteolione.tteolione.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import site.tteolione.tteolione.domain.user.User;

/*
*  Security Context의 인증 객체로부터 다양한 정보를 뽑아서 제공하는 클래스
* */
@Slf4j
public abstract class SecurityUtils {

    public static Long getUserId() {
        log.debug("{} Authentication 유저 아이디", ((SecurityUserDto)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUserId());
        return ((SecurityUserDto)(SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getUserId();
    }

    public static SecurityUserDto getUser() {
        log.debug("{} Authentication 유저", (SecurityUserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return (SecurityUserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
