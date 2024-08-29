package ai.agreement.AiProject.controller;

import ai.agreement.AiProject.dto.response.ResultAndData;
import ai.agreement.AiProject.dto.response.SuccessAndData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "연결 확인", description = "최초 접속 시 요청용 jwt 발급 용도(운영용)")
@RequestMapping(value = "/api/v1/connection", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionController {

    private final PasswordEncoder passwordEncoder;

    @Value("${security.api-key}")
    private String API_KEY_VALUE;

    @Value("${security.is-test:true}")
    private boolean isTestMode;

    @Operation(summary = "인증키 발급", description = "최초 접속 시 브라우저용 인증키 발급<br>테스트 모드에서는 API KEY 를 암호화한 인증키 제공<br>테스트 모드가 아닐경우 JWT Token 생성 후 응답 헤더로 제공, 프론트에서 쿠키로 저장 후 POST 요청 시마다 요청 Authorization 헤더에 포함<br>테스트모드: false")
    @GetMapping
    public ResultAndData getHashedApiKey() {
        if (isTestMode) {
            return new SuccessAndData<>(HttpStatus.OK.getReasonPhrase(), passwordEncoder.encode(API_KEY_VALUE));
        }
        return new SuccessAndData<>(HttpStatus.OK.getReasonPhrase(), "Set Auth in Header");
    }
}
