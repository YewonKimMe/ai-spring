package ai.agreement.AiProject.security;

import ai.agreement.AiProject.dto.response.FailAndData;
import ai.agreement.AiProject.dto.response.ResultAndData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.debug("AuthenticationEntryPoint, unauthenticated request detected");
        log.info("AuthenticationEntryPoint, unauthenticated request detected, path={}", request.getServletPath());
        Exception e = (Exception) request.getAttribute("exception");

        String errMsg = null;
        if (e != null) {
            log.debug("exception: {}", e.getMessage());
            errMsg = e.getMessage();
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        ResultAndData messageObj = new FailAndData(HttpStatus.UNAUTHORIZED.getReasonPhrase(), errMsg);
        String resultMessage = objectMapper.writeValueAsString(messageObj);
        response.getWriter().write(resultMessage);
    }
}
