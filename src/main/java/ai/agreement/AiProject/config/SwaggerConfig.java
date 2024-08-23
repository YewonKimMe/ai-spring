package ai.agreement.AiProject.config;

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
                .info(apiInformation());
    }

    private Info apiInformation() {
        return new Info()
                .title("OCR 기반 계약서 평가 AI")
                .description("prompt 기반의 계약서 이미지 ocr 추출 후 평가")
                .version("1.0.0");
    }
}
