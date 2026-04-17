package com.liyh.agent.config;

import com.liyh.system.config.AiProperties;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChain4jConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(AiProperties props) {
        AiProperties.Chat chat = props.getChat();
        return OpenAiChatModel.builder()
                .baseUrl(chat.getBaseUrl())
                .apiKey(chat.getApiKey())
                .modelName(chat.getModel())
                .maxTokens(chat.getMaxTokens())
                .temperature(chat.getTemperature())
                .timeout(Duration.ofSeconds(60))
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(AiProperties props) {
        AiProperties.Embedding emb = props.getEmbedding();
        return OpenAiEmbeddingModel.builder()
                .baseUrl(emb.getBaseUrl())
                .apiKey(emb.getApiKey())
                .modelName(emb.getModel())
                .timeout(Duration.ofSeconds(30))
                .build();
    }
}
