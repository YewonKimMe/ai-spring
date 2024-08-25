package ai.agreement.AiProject.config;

import ai.agreement.AiProject.service.GoogleVisionOcrService;
import ai.agreement.AiProject.service.OcrService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {
    @Bean
    public OcrService ocrService() {
        return new GoogleVisionOcrService();
    }
}
