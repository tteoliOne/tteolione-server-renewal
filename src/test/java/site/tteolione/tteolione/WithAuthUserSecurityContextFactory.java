//package site.tteolione.tteolione;
//
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.test.context.support.WithSecurityContextFactory;
//
//import java.util.List;
//
//public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {
//
//    @Override
//    public SecurityContext createSecurityContext(WithAuthUser annotation) {
//        String loginId = annotation.loginId();
//        String role = annotation.role();
//
//        User user = new User(loginId, "password", List.of(new SimpleGrantedAuthority(role)));
//        UsernamePasswordAuthenticationToken token =
//                new UsernamePasswordAuthenticationToken(user, "password", user.getAuthorities());
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(token);
//        return context;
//    }
//}
//
