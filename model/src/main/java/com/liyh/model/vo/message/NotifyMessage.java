package com.liyh.model.vo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统通知消息实体
 *
 * @Author LiYH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 发送者用户名
     */
    private String fromUsername;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 通知类型
     */
    private NotifyType type;

    /**
     * 目标ID（帖子ID/评论ID）
     */
    private Long targetId;

    /**
     * 目标标题（帖子标题/评论内容摘要）
     */
    private String targetTitle;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 通知类型枚举
     */
    public enum NotifyType {
        /**
         * 点赞帖子
         */
        LIKE_POST,
        /**
         * 点赞评论
         */
        LIKE_COMMENT,
        /**
         * 评论帖子
         */
        COMMENT_POST,
        /**
         * 回复评论
         */
        REPLY_COMMENT,
        /**
         * 关注用户
         */
        FOLLOW,
        /**
         * 系统通知
         */
        SYSTEM
    }
}
