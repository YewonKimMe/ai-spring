package ai.agreement.AiProject.security;

import ai.agreement.AiProject.Tool.CookieUtil;
import ai.agreement.AiProject.enums.CookieName;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtGeneratorFilter extends OncePerRequestFilter {

    @Value("${security.jwt-secret}")
    private String jwtKey;

    @Value("${security.is-test:true}")
    private boolean isTestMode;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!CookieUtil.checkCookie(request, CookieName.USER_AUTH_KEY)) {
            SecretKey secretKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
            Instant expirationTime = Instant.now().plus(Duration.ofHours(3)); // 3시간뒤 만료

            // 일정 시간 후 만료를 위해서 jwt 사용
            String jwtToken = Jwts.builder()
                    .issuer("ai-contract")
                    .subject("user-request-token")
                    .issuedAt(new Date())
                    .expiration(Date.from(expirationTime))
                    .signWith(secretKey)
                    .compact();

            log.debug("jwt={}", jwtToken);
            response.setHeader(SecurityConst.AUTH_HEADER, jwtToken);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        log.debug("request path = {}", path);
        log.debug("pathMatcher Result={}", pathMatcher.match("/api/v1/connection", path));

        return (!pathMatcher.match("/api/v1/connection", path) || isTestMode); // 테스트 모드거나 connection 요청이 아니면 실행 X
    }
}
