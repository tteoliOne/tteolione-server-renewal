package site.tteolione.tteolione.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static site.tteolione.tteolione.config.jwt.GlobalConstants.*;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String jwt = resolveToken(request);
            String requestURI = request.getRequestURI();
            try{
                if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                }}
            catch (SecurityException | MalformedJwtException e) {
                request.setAttribute("exception", ExceptionCode.WRONG_TYPE_TOKEN.getCode());
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", ExceptionCode.EXPIRED_TOKEN.getCode());
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", ExceptionCode.UNSUPPORTED_TOKEN.getCode());
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", ExceptionCode.WRONG_TOKEN.getCode());
            } catch (Exception e) {
                log.error("================================================");
                log.error("JwtFilter - doFilterInternal() 오류발생");
                log.error("token : {}", jwt);
                log.error("Exception Message : {}", e.getMessage());
                log.error("Exception StackTrace : {");
                e.printStackTrace();
                log.error("}");
                log.error("================================================");
                request.setAttribute("exception", ExceptionCode.UNKNOWN_ERROR.getCode());
            }

            filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
