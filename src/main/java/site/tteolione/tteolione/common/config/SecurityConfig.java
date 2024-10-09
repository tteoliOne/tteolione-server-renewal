package site.tteolione.tteolione.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.tteolione.tteolione.common.config.jwt.JwtAccessDeniedHandler;
import site.tteolione.tteolione.common.config.jwt.JwtAuthenticationEntryPoint;
import site.tteolione.tteolione.common.config.jwt.JwtFilter;
import site.tteolione.tteolione.common.config.jwt.TokenProvider;
import site.tteolione.tteolione.domain.user.UserRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        http
                .csrf((auth) -> auth
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable());
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());
        //h2데이터베이스 x-frame-options(http 응답 헤더의 요소 렌더링 가능 판단) 이 기능을 disable
        http
                .headers((header) -> header.frameOptions((option) -> option.sameOrigin().disable()));
        //HTTP Basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());
        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/test").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/api/v2/email/**").permitAll()
                        .requestMatchers("/api/v2/users/check/**").permitAll()
                        .requestMatchers("/api/v2/users/find/**").permitAll()
                        .requestMatchers("/api/v2/users/verify/**").permitAll()
                        .requestMatchers("/api/v2/auth/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v2/products/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH ,"/api/v2/users/nickname").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH,"/api/v2/users/reset/password").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated());

        http
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler));
        http
                .addFilterBefore(new JwtFilter(tokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class);

        // 등등의 설정들 ...

        return http.build();
    }
}
