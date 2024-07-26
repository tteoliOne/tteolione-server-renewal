package site.tteolione.tteolione.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.tteolione.tteolione.config.jwt.JwtAccessDeniedHandler;
import site.tteolione.tteolione.config.jwt.JwtAuthenticationEntryPoint;
import site.tteolione.tteolione.config.jwt.JwtFilter;
import site.tteolione.tteolione.config.jwt.TokenProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
        http
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler));
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
        http
                .addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/docs/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers("/api/users/check/**").permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/users/**").hasRole("MEMBER")
                        .anyRequest().authenticated());


        // 등등의 설정들 ...

        return http.build();
    }
}
