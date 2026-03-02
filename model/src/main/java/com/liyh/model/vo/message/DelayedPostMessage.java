package com.liyh.model.vo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 延迟发布帖子消息
 * 用于定时发布功能
 *
 * @Author LiYH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayedPostMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 帖子ID（草稿ID）
     */
    private Long postId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;

    /**
     * 是否匿名
     */
    private Boolean anonymous;

    /**
     * 计划发布时间
     */
    private Date publishTime;

    /**
     * 消息创建时间
     */
    private Date createTime;

    /**
     * 延迟时间（毫秒）
     */
    private Long delayMillis;
}
