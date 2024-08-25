package ai.agreement.AiProject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ApiKeyValidatorFilter extends OncePerRequestFilter {

    @Value("${security.api-key}")
    private String API_KEY_VALUE;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String apiKey = request.getHeader(SecurityConst.AUTH_HEADER);

        if (API_KEY_VALUE.equals(apiKey)) {
            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(apiKey, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.error("Unauthorized API key={}", apiKey);
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
        return pathMatcher.match("/v3/api-docs/**", path) || pathMatcher.match("/swagger-ui/**", path);
    }
}
