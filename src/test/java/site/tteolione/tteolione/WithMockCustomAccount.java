package site.tteolione.tteolione;

import org.springframework.security.test.context.support.WithSecurityContext;
import site.tteolione.tteolione.domain.user.constants.ELoginType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomAccountSecurityContextFactory.class)
public @interface WithMockCustomAccount {

    String loginId() default "loginId";

    String username() default "username";

    String email() default "test-email";

    ELoginType loginType() default ELoginType.eApp;


}
