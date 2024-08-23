package ai.agreement.AiProject.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OcrService {

    String doImageOcr(MultipartFile file);

    String doImagesOcr(List<MultipartFile> files);

}
