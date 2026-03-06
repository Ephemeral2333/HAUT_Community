package com.liyh.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private boolean enabled = false;

    private Chat chat = new Chat();
    /** 轻量模型，用于 Query 改写等简单任务，不填则复用 chat */
    private Chat lightChat = null;
    private Embedding embedding = new Embedding();
    private Rerank rerank = new Rerank();
    private Rag rag = new Rag();

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

    @Data
    public static class Rerank {
        private String baseUrl = "https://dashscope.aliyuncs.com/api/v1/services/rerank/text-rerank/text-rerank";
        /** 不填则复用 embedding 的 apiKey */
        private String apiKey;
        private String model = "gte-rerank-v2";
        /** 返回的 top_n 结果数，0 表示返回全部 */
        private int topN = 5;
    }

    @Data
    public static class Rag {
        /** 是否启用 Query 改写（关闭可省去一次 LLM 调用） */
        private boolean queryRewriteEnabled = true;
        /** 是否启用 LLM Rerank（关闭可省去一次 LLM 调用） */
        private boolean rerankEnabled = true;
        /** 向量检索得分阈值（cosineSimilarity + 1.0，范围 0~2） */
        private double vectorScoreThreshold = 1.3;
        /** 单 IP 每分钟最大请求数，0 表示不限流 */
        private int rateLimitPerMinute = 10;
        /** 用户问题最大长度 */
        private int maxQuestionLength = 500;
    }
}
