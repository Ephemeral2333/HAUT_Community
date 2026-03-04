package com.liyh.model.vo.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultVo implements Serializable {

    private Long postId;
    private String title;
    private String content;
    private String highlightTitle;
    private String highlightContent;
    private Long userId;
    private List<String> tags;
    private Date createTime;
    private double score;
}
