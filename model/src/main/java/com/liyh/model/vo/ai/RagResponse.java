package com.liyh.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RagResponse implements Serializable {

    private String answer;
    private List<SourcePost> sources;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SourcePost implements Serializable {
        private Long postId;
        private String title;
        private double score;
    }
}
