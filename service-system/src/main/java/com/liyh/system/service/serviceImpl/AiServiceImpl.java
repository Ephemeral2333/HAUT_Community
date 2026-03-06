package com.liyh.system.service.serviceImpl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.liyh.system.config.AiProperties;
import com.liyh.system.service.AiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 服务实现 - 调用 OpenAI 兼容协议的 LLM / Embedding API
 * 支持 DeepSeek、通义千问、OpenAI 等
 */
@Service
@Slf4j
public class AiServiceImpl implements AiService {

    @Autowired
    private AiProperties aiProperties;

    private OkHttpClient httpClient;

    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_RETRIES = 2;
    private static final long RETRY_DELAY_MS = 1000;
    private static final int RERANK_DOC_MAX_LEN = 400;
    private static final Pattern JSON_ARRAY_PATTERN = Pattern.compile("\\[\\s*[\\s\\S]*?]");

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        log.info("AI 服务 OkHttpClient 初始化完成 (connectTimeout=15s, readTimeout=60s)");
    }

    @Override
    public boolean isAvailable() {
        return aiProperties.isEnabled()
                && aiProperties.getChat().getApiKey() != null
                && !aiProperties.getChat().getApiKey().isBlank();
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        if (!isAvailable()) {
            log.warn("AI 服务未配置，跳过调用");
            return null;
        }

        AiProperties.Chat chatConfig = aiProperties.getChat();

        JSONObject body = new JSONObject();
        body.put("model", chatConfig.getModel());
        body.put("max_tokens", chatConfig.getMaxTokens());
        body.put("temperature", chatConfig.getTemperature());

        JSONArray messages = new JSONArray();
        if (systemPrompt != null && !systemPrompt.isBlank()) {
            JSONObject sysMsg = new JSONObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);
        }
        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        body.put("messages", messages);

        String url = chatConfig.getBaseUrl() + "/chat/completions";

        log.info("AI Chat 调用开始 - url: {}, model: {}", url, chatConfig.getModel());
        long start = System.currentTimeMillis();
        try {
            String responseBody = doPostWithRetry(url, chatConfig.getApiKey(), body.toJSONString());
            long elapsed = System.currentTimeMillis() - start;
            log.info("AI Chat 调用成功 - 耗时: {}ms, 响应长度: {}", elapsed, responseBody.length());
            JSONObject json = JSON.parseObject(responseBody);
            String content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            log.info("AI Chat 返回内容: {}", content);
            return content;
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("AI Chat 调用失败 - 耗时: {}ms, error: {}", elapsed, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public float[] embed(String text) {
        List<float[]> results = embedBatch(List.of(text));
        return results != null && !results.isEmpty() ? results.get(0) : null;
    }

    @Override
    public List<float[]> embedBatch(List<String> texts) {
        if (!isAvailable()) {
            log.warn("AI 服务未配置，跳过 Embedding");
            return null;
        }

        AiProperties.Embedding embConfig = aiProperties.getEmbedding();
        String apiKey = embConfig.getApiKey() != null ? embConfig.getApiKey() : aiProperties.getChat().getApiKey();

        JSONObject body = new JSONObject();
        body.put("model", embConfig.getModel());
        body.put("input", texts);
        if (embConfig.getDimensions() > 0) {
            body.put("dimensions", embConfig.getDimensions());
        }

        String url = embConfig.getBaseUrl() + "/embeddings";

        try {
            String responseBody = doPostWithRetry(url, apiKey, body.toJSONString());
            JSONObject json = JSON.parseObject(responseBody);
            JSONArray dataArray = json.getJSONArray("data");

            List<float[]> results = new ArrayList<>();
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray embeddingArr = dataArray.getJSONObject(i).getJSONArray("embedding");
                float[] vector = new float[embeddingArr.size()];
                for (int j = 0; j < embeddingArr.size(); j++) {
                    vector[j] = embeddingArr.getFloatValue(j);
                }
                results.add(vector);
            }
            return results;
        } catch (Exception e) {
            log.error("Embedding 调用失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 带重试的 HTTP POST，对 5xx 和网络异常重试最多 MAX_RETRIES 次
     */
    private String doPostWithRetry(String url, String apiKey, String jsonBody) throws IOException {
        IOException lastException = null;
        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                return doPost(url, apiKey, jsonBody);
            } catch (IOException e) {
                lastException = e;
                boolean serverError = e.getMessage() != null && e.getMessage().contains("[5");
                boolean networkError = e.getMessage() != null &&
                        (e.getMessage().contains("timeout") || e.getMessage().contains("connect"));
                if (attempt < MAX_RETRIES && (serverError || networkError)) {
                    log.warn("API 调用失败(第{}次), {}ms 后重试: {}", attempt + 1, RETRY_DELAY_MS, e.getMessage());
                    try {
                        Thread.sleep(RETRY_DELAY_MS * (attempt + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                } else {
                    throw e;
                }
            }
        }
        throw lastException;
    }

    private String doPost(String url, String apiKey, String jsonBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, JSON_MEDIA))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "empty";
                throw new IOException("API 调用失败 [" + response.code() + "]: " + errorBody);
            }
            return response.body() != null ? response.body().string() : "";
        }
    }

    // ==================== Query 改写 ====================

    @Override
    public List<String> rewriteQuery(String question) {
        if (!isAvailable())
            return Collections.emptyList();

        String systemPrompt = """
                你是搜索查询优化专家。给定用户问题，生成2个语义相关但表达方式不同的搜索查询，以提高检索召回率。
                严格只返回JSON字符串数组，不要包含任何其他文字或代码块标记。
                格式示例：["查询1", "查询2"]
                """;

        String response = chat(systemPrompt, question);
        if (response == null || response.isBlank())
            return Collections.emptyList();

        try {
            List<String> result = JSON.parseArray(extractJsonArray(response), String.class);
            log.info("Query 改写结果: {}", result);
            return result != null ? result : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Query 改写解析失败: {}, 原始响应: {}", e.getMessage(), response);
            return Collections.emptyList();
        }
    }

    // ==================== Reranker ====================

    @Override
    public List<Integer> rerankIndices(String query, List<String> documents) {
        if (!isAvailable() || documents == null || documents.isEmpty())
            return null;

        StringBuilder docsBuilder = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            String text = documents.get(i);
            if (text != null && text.length() > RERANK_DOC_MAX_LEN)
                text = text.substring(0, RERANK_DOC_MAX_LEN) + "...";
            docsBuilder.append(i).append(". ").append(text).append("\n");
        }

        String systemPrompt = """
                你是信息检索专家。给定用户问题和候选文档列表，请按文档与问题相关性从高到低排列文档索引（0-based）。
                严格只返回JSON整数数组，不要包含任何其他文字或代码块标记。
                格式示例：[2, 0, 1] 表示第2篇最相关，第0篇次之，第1篇最不相关。
                """;
        String userMsg = "问题：" + query + "\n\n候选文档：\n" + docsBuilder;

        String response = chat(systemPrompt, userMsg);
        if (response == null || response.isBlank())
            return null;

        try {
            List<Integer> indices = JSON.parseArray(extractJsonArray(response), Integer.class);
            log.info("Reranker 排序结果: {}", indices);
            return indices;
        } catch (Exception e) {
            log.warn("Reranker 解析失败: {}, 原始响应: {}", e.getMessage(), response);
            return null;
        }
    }

    /**
     * 从 LLM 响应中提取 JSON 数组，兼容 markdown 代码块包裹和前后附带文字的情况
     */
    private String extractJsonArray(String response) {
        String cleaned = response.trim()
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*", "")
                .trim();
        if (cleaned.startsWith("[")) {
            return cleaned;
        }
        Matcher matcher = JSON_ARRAY_PATTERN.matcher(cleaned);
        if (matcher.find()) {
            return matcher.group();
        }
        return cleaned;
    }
}
