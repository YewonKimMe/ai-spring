package ai.agreement.AiProject.controller;

import ai.agreement.AiProject.ai.Assistant;
import ai.agreement.AiProject.dto.DefaultChatMessage;
import ai.agreement.AiProject.dto.response.ResultAndData;
import ai.agreement.AiProject.dto.response.SuccessAndData;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping(value = "/api/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class AiController {

    @Value("${spring.lang-chain.open-ai.api-key}")
    String apiKey;

    final String requirement = "다음 계약서에 대한 평가를 수행해 주세요. 계약서 내용: {text_data}\n\n" +
            "기준:\n" +
            "1. 기본 정보의 정확성 (20점 만점)\n" +
            "2. 법적 효력 (20점 만점)\n" +
            "3. 특약 사항의 완성도 (20점 만점)\n" +
            "4. 양측 권리와 의무의 명확성 (20점 만점)\n" +
            "5. 기타 중요한 조항 (20점 만점)\n" +
            "각 항목의 점수를 구체적으로 설명 및 제공해 주세요. 각 항목의 점수는 매우 엄격하게 부여하고 그리고 설명이 끝난 이후, ; 구분자 이후로 각 1~5번 점수를 ;로 구별하여 붙여주세요. 예를 들면, ~설명;15;20;20;30;20";

    @GetMapping
    public ResponseEntity<ResultAndData> getDefaultChat(@RequestBody DefaultChatMessage defaultChatMessage) {

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("gpt-4")
                        .temperature(0.01)
                        .build())
                .chatMemory(chatMemory)
                .build();
        String generatedMessage = assistant.chat(requirement +" 계약서 내용: "+defaultChatMessage.getMessage());
        log.debug("gen message={}", generatedMessage);
        return ResponseEntity
                .ok()
                .body(new SuccessAndData(HttpStatus.OK.getReasonPhrase(), generatedMessage));

    }

    @GetMapping("/streaming")
    public ResponseBodyEmitter getMessageStreamingResult(@RequestBody DefaultChatMessage defaultChatMessage) {

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.withApiKey(apiKey);
        String userMessage = defaultChatMessage.getMessage();

        // 비동기적으로 스트리밍 데이터 생성
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                model.generate(requirement + userMessage, new StreamingResponseHandler<AiMessage>() {

                    @Override
                    public void onNext(String token) {
                        try {
                            // 클라이언트에게 전송할 데이터
                            emitter.send(token);
                            //System.out.println("onNext: " + token);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onComplete(Response<AiMessage> response) {
                        try {
                            emitter.complete();
                            //System.out.println("onComplete: " + response);
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        emitter.completeWithError(error);
                        //error.printStackTrace();
                    }
                });
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }

}
