package site.tteolione.tteolione;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import site.tteolione.tteolione.common.util.SecurityUserDto;
import site.tteolione.tteolione.domain.user.constants.EAuthority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithMockCustomAccountSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomAccount> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomAccount customOAuth2Account) {
        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("loginId", customOAuth2Account.loginId());
        attributes.put("username", customOAuth2Account.username());
        attributes.put("email", customOAuth2Account.email());

        final SecurityUserDto principal = SecurityUserDto.builder()
                .userId(1L)
                .email(customOAuth2Account.email())
                .userRole(EAuthority.ROLE_USER)
                .build();

        final Authentication token = new UsernamePasswordAuthenticationToken(principal, "", List.of(new SimpleGrantedAuthority(EAuthority.ROLE_USER.name())));

        context.setAuthentication(token);
        return context;
    }
}
