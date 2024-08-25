package ai.agreement.AiProject.service;

import ai.agreement.AiProject.exception.IllegalExtensionException;
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
                throw new IllegalExtensionException("올바르지 않은 파일 확장자 입니다.\n전송된 확장자: " + extension + ", 파일명: " + fileName);
            }
        }

        List<AnnotateImageRequest> requests = new ArrayList<>();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            for (MultipartFile file : files) {
                byte[] data = file.getBytes();

                ByteString imgBytes = ByteString.copyFrom(data);

                Image img = Image.newBuilder().setContent(imgBytes).build();

                Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

                AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

                requests.add(request);
            }

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            StringBuilder sb = new StringBuilder();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    log.error("Error: {}", res.getError().getMessage());
                    return null;
                }
                for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                    sb.append(annotation.getDescription());
                }
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
