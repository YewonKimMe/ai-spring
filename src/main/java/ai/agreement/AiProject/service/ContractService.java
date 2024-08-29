package ai.agreement.AiProject.service;

import ai.agreement.AiProject.ai.result.Result;
import ai.agreement.AiProject.ai.result.StringResult;
import ai.agreement.AiProject.enums.IntParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {

    private final OcrService ocrService; // OCR 서비스

    private final GenAIService genAIService; // Gen AI 서비스

    // 계약서 분석 절차 수행 메소드
    public Result contractCheckProcess(IntParam code, List<MultipartFile> files) {

        String contractOcrResult = ocrService.doImagesOcr(files);

        if (code == IntParam.ZERO) { // 문자열 응답 요청일 경우
            String chatResponse = genAIService.createChatRequest(contractOcrResult);
            return new StringResult(chatResponse);
        } else { // 구조화된 응답 요청일 경우
            return genAIService.createAnalysisResult(contractOcrResult);
        }
    }
}
