package ai.agreement.AiProject.service;

import ai.agreement.AiProject.enums.ErrorMessage;
import ai.agreement.AiProject.exception.IllegalExtensionException;
import ai.agreement.AiProject.exception.ImageOcrException;
import ai.agreement.AiProject.exception.TextNotFoundException;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
public class GoogleVisionOcrService implements OcrService {

    String[] allowedExtensions = {"jpg", "png", "jpeg"};

    @Override
    public String doImageOcr(MultipartFile file) {

        log.debug("environment key={}", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            List<AnnotateImageRequest> requests = new ArrayList<>();

            byte[] data = file.getBytes();
            ByteString imgBytes = ByteString.copyFrom(data);

            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

            requests.add(request);

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            StringBuilder sb = new StringBuilder();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.getError().getMessage());
                    return null;
                }
                sb.append(res.getTextAnnotationsList().get(0).getDescription());
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public String doImagesOcr(List<MultipartFile> files) {

        log.debug("environment key={}", System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));

        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

            log.debug("fileName={}", fileName);
            log.debug("extension={}", extension);

            if (!List.of(allowedExtensions).contains(extension)) {
                throw new IllegalExtensionException(ErrorMessage.ILLEGAL_EXTENSION.getErrorMessage() + "\n전송된 확장자: " + extension + ", 파일명: " + fileName);
            }
        }

        List<AnnotateImageRequest> requests = new ArrayList<>();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            for (MultipartFile file : files) {
                byte[] data = file.getBytes(); // Mulitipartfile 을 byte array 로 변환

                ByteString imgBytes = ByteString.copyFrom(data);

                Image img = Image.newBuilder().setContent(imgBytes).build();

                Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

                AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

                requests.add(request); // 요청 리스트에 추가
            }

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests); // 이미지 추출 실행
            List<AnnotateImageResponse> responses = response.getResponsesList();

            StringBuilder sb = new StringBuilder();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) { // 이미지 응답에 오류가 있을 경우
                    log.error("Error: {}", res.getError().getMessage());
                    throw new ImageOcrException(ErrorMessage.IMAGE_OCR.getErrorMessage());
                }
                if (res.getTextAnnotationsList().isEmpty()) { // 식별된 텍스트가 존재하지 않을 경우
                    throw new TextNotFoundException(ErrorMessage.TEXT_NOT_FOUND.getErrorMessage());
                }
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    sb.append(annotation.getDescription()); // 추출된 텍스트를 StringBuilder 에 추가
                }
            }
            return sb.toString();

        } catch (TextNotFoundException | ImageOcrException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
