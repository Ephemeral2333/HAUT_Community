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
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            String responseBody = doPost(url, chatConfig.getApiKey(), body.toJSONString());
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
            String responseBody = doPost(url, apiKey, body.toJSONString());
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
}
