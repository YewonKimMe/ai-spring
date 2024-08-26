package ai.agreement.AiProject.service;

import ai.agreement.AiProject.ai.Assistant;
import ai.agreement.AiProject.exception.IllegalContractTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OpenAiContractCheckService implements GenAIService {

    private final Assistant assistant;

    @Override
    public String createChatRequest(String message) {

        final String prefix = "계약서 OCR 내용: ";

        String response = assistant.chat(prefix + message);

        if (response.equals("-1")) {
            throw new IllegalContractTypeException("계약서 양식이 아닙니다.");
        }

        return response;
    }
}
