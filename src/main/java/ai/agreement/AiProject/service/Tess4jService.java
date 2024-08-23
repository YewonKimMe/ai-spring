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
import java.io.IOException;
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
        tesseract.setDatapath("src/main/resources/tessdata"); //** 학습된데이터가 있는 폴더를 지정해준다.
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
