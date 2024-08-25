package ai.agreement.AiProject.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OcrConfig {
    @Bean
    public OcrService ocrService() {
        return new GoogleVisionOcrService();
    }
}
