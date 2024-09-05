package ai.agreement.AiProject.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class ApiKeyValidatorFilter extends OncePerRequestFilter {

    @Value("${security.api-key}")
    private String API_KEY_VALUE;

    @Value("${security.is-test:true}")
    private boolean isTestMode;

    @Value("${security.jwt-secret}")
    private String jwtKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * 2가지 API KEY 사용 가능
         * 1. 개발용 API KEY
         * 2. 웹사이트용 JWT
         * */
        String apiKey = request.getHeader(SecurityConst.AUTH_HEADER);

        if (API_KEY_VALUE.equals(apiKey)) { // 개발용 API KEY 인 경우, 인증 완료
            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(apiKey, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

            return;
        }
        try { // 웹사이트용 JWT
            // 기존 비밀키 생성
            SecretKey existingKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(existingKey) // 기존 비밀키
                    .build()
                    .parseSignedClaims(apiKey)// Front 에서 전송한 jwt, 기존 비밀키와 일치 여부 확인, 일치하지 않으면 exception 발생
                    .getPayload();

            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(apiKey, null, null);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Unauthorized API key={}, path={}", apiKey, request.getServletPath());
            request.setAttribute("exception", new BadCredentialsException("유효하지 않은 인증 정보(API KEY) 입니다."));
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        log.debug("ApiKeyValidatorFilter path={} matches?={}", path, pathMatcher.match("/v3/api-docs/**", path));
        return pathMatcher.match("/v3/api-docs/**", path) || pathMatcher.match("/swagger-ui/**", path) || (pathMatcher.match("/api/v1/connection", path));
    }
}
