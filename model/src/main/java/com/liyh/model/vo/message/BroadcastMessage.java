package com.liyh.model.vo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 广播消息实体
 * 用于系统公告等全站广播
 *
 * @Author LiYH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 广播类型
     */
    private BroadcastType type;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联的目标ID（如公告ID）
     */
    private Long targetId;

    /**
     * 发送者ID（管理员ID）
     */
    private Long senderId;

    /**
     * 发送者用户名
     */
    private String senderName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 广播类型枚举
     */
    public enum BroadcastType {
        /**
         * 系统公告
         */
        ANNOUNCEMENT,
        /**
         * 系统维护通知
         */
        MAINTENANCE,
        /**
         * 活动通知
         */
        ACTIVITY,
        /**
         * 紧急通知
         */
        URGENT
    }
}
