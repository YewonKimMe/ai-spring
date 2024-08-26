package ai.agreement.AiProject.ai;

import ai.agreement.AiProject.service.GenAIService;
import ai.agreement.AiProject.service.OpenAiContractCheckService;
import dev.ai4j.openai4j.embedding.EmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${spring.lang-chain.open-ai.api-key}")
    String apiKey;

    @Value("${spring.lang-chain.open-ai.model-name}")
    String modelName;

    @Bean
    public Assistant assistant() {
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

    @Bean
    public GenAIService genAIService() {
        return new OpenAiContractCheckService(assistant());
    }
}
