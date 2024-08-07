package site.tteolione.tteolione;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

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

        final Authentication token = new UsernamePasswordAuthenticationToken("test123", "dd", List.of(new SimpleGrantedAuthority("ROLE_USEr")));

        context.setAuthentication(token);
        return context;
    }
}
