package com.liyh.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 服务配置（兼容 OpenAI 协议的 API）
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private boolean enabled = false;

    private Chat chat = new Chat();
    private Embedding embedding = new Embedding();

    @Data
    public static class Chat {
        private String baseUrl = "https://api.deepseek.com/v1";
        private String apiKey;
        private String model = "deepseek-chat";
        private int maxTokens = 2048;
        private double temperature = 0.3;
    }

    @Data
    public static class Embedding {
        private String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1";
        private String apiKey;
        private String model = "text-embedding-v4";
        private int dimensions = 1024;
    }
}
