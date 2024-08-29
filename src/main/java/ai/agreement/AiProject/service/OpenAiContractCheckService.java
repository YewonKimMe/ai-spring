package ai.agreement.AiProject.service;

import ai.agreement.AiProject.ai.StructuredContractAnalysisGenerator;
import ai.agreement.AiProject.ai.result.structed.ContractAnalysisResult;
import ai.agreement.AiProject.enums.ErrorMessage;
import ai.agreement.AiProject.ai.Assistant;
import ai.agreement.AiProject.exception.IllegalContractTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OpenAiContractCheckService implements GenAIService {

    private final Assistant assistant;

    private final StructuredContractAnalysisGenerator structuredContractAnalysisGenerator;

    @Override
    public String createChatRequest(String message) {

        final String prefix = "계약서 OCR 내용: ";

        String response = assistant.chat(prefix + message);

        if (response.equals("-1")) {
            throw new IllegalContractTypeException(ErrorMessage.NOT_CONTRACT.getErrorMessage());
        }

        return response;
    }

    @Override
    public ContractAnalysisResult createAnalysisResult(String message) {

        ContractAnalysisResult response = structuredContractAnalysisGenerator.generateStructuredResult(message);

        log.debug("response={}", response);
        if (!response.isContract()) {
            throw new IllegalContractTypeException(ErrorMessage.NOT_CONTRACT.getErrorMessage());
        }

        return response;
    }
}
