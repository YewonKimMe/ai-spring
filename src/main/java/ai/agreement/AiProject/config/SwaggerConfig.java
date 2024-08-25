package ai.agreement.AiProject.config;

import ai.agreement.AiProject.security.SecurityConst;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("apiKeyAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name(SecurityConst.AUTH_HEADER)))
                .info(apiInformation())
                .addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"));
    }

    private Info apiInformation() {
        return new Info()
                .title("OCR 기반 계약서 평가 AI")
                .description("prompt 기반의 계약서 이미지 ocr 추출 후 평가<br><br>API 문서: Authorize 에 API KEY 입력<br><br>프론트 앱: Http 요청 시 Authorization 헤더에 API KEY 포함<br><br>OCR Version: google-cloud-vision:3.46.0")
                .version("1.0.0");
    }
}
