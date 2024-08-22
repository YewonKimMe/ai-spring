package ai.agreement.AiProject.service;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {

    String doImageOcr(MultipartFile file);

}
