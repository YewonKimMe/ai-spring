package ai.agreement.AiProject.controller;

import ai.agreement.AiProject.ai.Assistant;
import ai.agreement.AiProject.dto.DefaultChatMessage;
import ai.agreement.AiProject.dto.response.ResultAndData;
import ai.agreement.AiProject.dto.response.SuccessAndData;
import ai.agreement.AiProject.service.OcrService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class AiController {

    @Value("${spring.lang-chain.open-ai.api-key}")
    String apiKey;

    private final OcrService ocrService;

//    final String requirement = "다음 계약서에 대한 평가를 수행해 주세요.\n" +
//            "기준:\n" +
//            "1. 기본 정보의 정확성 (20점 만점)\n" +
//            "2. 법적 효력 (20점 만점)\n" +
//            "3. 특약 사항의 완성도 (20점 만점)\n" +
//            "4. 양측 권리와 의무의 명확성 (20점 만점)\n" +
//            "5. 기타 중요한 조항 (20점 만점)\n" +
//            "출력 방법 설명:"+
//            "계약서 제목을 간단하게 언급하고, 임대인은(회사)는 누구인지 언급해주세요. 각 항목의 점수를 구체적으로 설명 및 제공해 주세요. 평가내용은 높임말을 사용합니다." +
//            "응답 양식은 다음과 같습니다. 응답 양식을 철저하게 지켜주세요. AI가 생성한 응답은 {} 내에 위치하고, 중괄호'{}'는 출력하지 않습니다. 계약서 OCR 내용은 출력하지 않습니다." +
//            "{계약서 제목}\n" +
//            "{임대인 정보}\n" +
//            "1. 기본 정보의 정확성 (20점 만점)\n{1항 평가내용}" +
//            "2. 법적 효력 (20점 만점)\n{2항 평가내용}" +
//            "3. 특약 사항의 완성도 (20점 만점)\n{3항 평가내용}" +
//            "4. 양측 권리와 의무의 명확성 (20점 만점)\n{4항 평가내용}" +
//            "5. 기타 중요한 조항 (20점 만점)\n{5항 평가내용}" +
//            "총평: {총평 내용}\n" +
//            "@{1항 평가 점수;2항 평가 점수;3항 평가 점수;4항 평가 점수;5항 평가 점수}";

    @GetMapping
    public ResponseEntity<ResultAndData> getDefaultChat(@RequestBody DefaultChatMessage defaultChatMessage) {

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName("gpt-3.5-turbo")
                        .temperature(0.01)
                        .build())
                .chatMemory(chatMemory)
                .build();
        String generatedMessage = assistant.chat("계약서 내용: "+defaultChatMessage.getMessage());
        log.debug("gen message={}", generatedMessage);
        return ResponseEntity
                .ok()
                .body(new SuccessAndData(HttpStatus.OK.getReasonPhrase(), generatedMessage));

    }

    @GetMapping("/agreement-image")
    public ResponseEntity<String> getOcrString(@RequestPart(name = "image") MultipartFile file) {

        Assistant assistant = this.setAssistant("gpt-4o-mini");

        String convertedString = ocrService.doImageOcr(file);
        String generatedMessage = assistant.chat("계약서 OCR 내용: " + convertedString);
        return ResponseEntity.
                ok()
                .body(generatedMessage);
    }

    private Assistant setAssistant(String modelName) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(OpenAiChatModel.builder()
                        .apiKey(apiKey)
                        .modelName(modelName)
                        .temperature(0.01)
                        .build())
                .chatMemory(chatMemory)
                .build();
    }

//    @GetMapping("/streaming")
//    public Flux<String> getMessageStreamingResult(@RequestBody DefaultChatMessage defaultChatMessage) {
//
//        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
//
//        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
//
//        Assistant assistant = AiServices.builder(Assistant.class)
//                .chatLanguageModel(OpenAiChatModel.builder()
//                        .apiKey(apiKey)
//                        .modelName("gpt-4")
//                        .temperature(0.01)
//                        .build())
//                .chatMemory(chatMemory)
//                .streamingChatLanguageModel(OpenAiStreamingChatModel.withApiKey(apiKey))
//                .build();
//
//        TokenStream tokenStream = assistant.chatStream(defaultChatMessage.getMessage());
//        tokenStream
//                .onNext(sink::tryEmitNext)
//                .onError(sink::tryEmitError)
//                .start();
//        return sink.asFlux();
////        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
////
////        StreamingChatLanguageModel model = OpenAiStreamingChatModel.withApiKey(apiKey);
////        String userMessage = defaultChatMessage.getMessage();
////
////        // 비동기적으로 스트리밍 데이터 생성
////        Executors.newSingleThreadExecutor().submit(() -> {
////            try {
////                model.generate(requirement + userMessage, new StreamingResponseHandler<AiMessage>() {
////
////                    @Override
////                    public void onNext(String token) {
////                        try {
////                            // 클라이언트에게 전송할 데이터
////                            emitter.send(token);
////                            //System.out.println("onNext: " + token);
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                    }
////
////                    @Override
////                    public void onComplete(Response<AiMessage> response) {
////                        try {
////                            emitter.complete();
////                            //System.out.println("onComplete: " + response);
////                        } catch (Exception e) {
////                            emitter.completeWithError(e);
////                        }
////                    }
////
////                    @Override
////                    public void onError(Throwable error) {
////                        emitter.completeWithError(error);
////                        //error.printStackTrace();
////                    }
////                });
////            } catch (Exception ex) {
////                emitter.completeWithError(ex);
////            }
////        });
////
////        return emitter;
//    }

}
