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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "OCR / Chatbot", description = "OCR / 챗봇 관련 기능, 허용 이미지 확장자: jpg, png, jpeg")
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class AiController {

    @Value("${spring.lang-chain.open-ai.api-key}")
    String apiKey;

    private final OcrService ocrService;

    private final Assistant assistant;

    @Operation(summary = "OCR 결과 확인 API", description = "OCR 테스트 API, 스트리밍을 지원하지 않기 때문에 수 초 내의 응답지연이 있을 수 있음<br>OCR Version: google-cloud-vision:3.46.0")
    @PostMapping(value = "/test/ocr-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultAndData> getOcrText(@RequestPart(name = "images") List<MultipartFile> files) {

        String OcrAgreementText = ocrService.doImagesOcr(files);

        return ResponseEntity.
                ok()
                .body(new SuccessAndData(HttpStatus.OK.getReasonPhrase(), OcrAgreementText));
    }

    @Operation(summary = "단순 텍스트 기반 계약서에 대한 GenAI 분석", description = "gpt-3.5, text input에 대한 gpt 응답<br>스트리밍 구현이 완료되지 않았기 때문에 10~20초의 응답지연이 있을 수 있음")
    @PostMapping("/chat")
    public ResponseEntity<ResultAndData> getDefaultChat(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "텍스트 계약서") @RequestBody DefaultChatMessage defaultChatMessage) {

        String generatedMessage = assistant.chat("계약서 내용: "+defaultChatMessage.getMessage());
        log.debug("gen message={}", generatedMessage);
        return ResponseEntity
                .ok()
                .body(new SuccessAndData(HttpStatus.OK.getReasonPhrase(), generatedMessage));

    }

    @Operation(summary = "계약서 이미지 OCR 후 응답", description = "gpt-4o, 이미지 ocr 처리 후 기반으로 정해진 양식에 따라 gpt 응답, 이미지 여러장 OCR 추출 가능<br>스트리밍 구현이 완료되지 않았기 때문에 업로드 분량에 따라 5~20초의 응답지연이 있을 수 있음<br>OCR Version: google-cloud-vision:3.46.0")
    @PostMapping(value = "/chat/agreement-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultAndData> getOcrString(@RequestPart(name = "images") List<MultipartFile> files) {

        String OcrAgreementText = ocrService.doImagesOcr(files);

        String generatedMessage = assistant.chat("계약서 OCR 내용: " + OcrAgreementText);

        return ResponseEntity.
                ok()
                .body(new SuccessAndData(HttpStatus.OK.getReasonPhrase(), generatedMessage));
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
