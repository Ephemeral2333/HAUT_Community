package com.liyh.system.service;

import java.util.List;
import java.util.function.Consumer;

public interface AiService {

    String chat(String systemPrompt, String userMessage);

    String chat(String systemPrompt, String userMessage, int maxTokens);

    void chatStream(String systemPrompt, String userMessage, int maxTokens,
                    Consumer<String> onToken, Consumer<String> onComplete, Consumer<Throwable> onError);

    float[] embed(String text);

    List<float[]> embedBatch(List<String> texts);

    List<String> rewriteQuery(String question);

    List<Integer> rerankIndices(String query, List<String> documents);

    boolean isAvailable();
}
