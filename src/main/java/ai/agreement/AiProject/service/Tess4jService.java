package ai.agreement.AiProject.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class Tess4jService implements OcrService {

    private final Tesseract tesseract = setTesseract();

//    @Value("${ocr.tessdata}")
//    private String tessdataPrefix;

    public String doImageOcr(MultipartFile file) {
        // MultipartFile을 BufferedImage로 변환
        BufferedImage bufferedImage = convertMultipartFileToBufferedImage(file);
        try {
            return tesseract.doOCR(bufferedImage);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }

    public String doImagesOcr(List<MultipartFile> files) {

        StringBuilder sb = new StringBuilder();

        for (MultipartFile file : files) {
            BufferedImage bufferedImage = convertMultipartFileToBufferedImage(file);
            try {
                String ocrTxt = tesseract.doOCR(bufferedImage);
                sb.append(ocrTxt);
            } catch (TesseractException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }

    private Tesseract setTesseract() {
        Tesseract tesseract = new Tesseract();
        try {
            // 자원 URL을 가져와서 File 객체를 생성하는 대신
            URL tessdataUrl = Tess4jService.class.getClassLoader().getResource("tessdata");
            if (tessdataUrl == null) {
                throw new RuntimeException("Tessdata resource not found.");
            }

            // URL에서 경로를 가져오고 이를 File로 변환
            File tessdataDir = new File(tessdataUrl.toURI());
            if (!tessdataDir.exists() || !tessdataDir.isDirectory()) {
                throw new RuntimeException("Tessdata directory not found or not a directory.");
            }

            // Tesseract의 데이터 경로 설정
            tesseract.setDatapath(tessdataDir.getAbsolutePath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error converting URL to URI", e);
        } catch (Exception e) {
            throw new RuntimeException("Error setting up Tesseract", e);
        }

        tesseract.setLanguage("kor"); // 언어설정
        tesseract.setPageSegMode(1); // 페이지 모드 설정
        tesseract.setOcrEngineMode(1);

        return tesseract;
    }

    private BufferedImage convertMultipartFileToBufferedImage(MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
