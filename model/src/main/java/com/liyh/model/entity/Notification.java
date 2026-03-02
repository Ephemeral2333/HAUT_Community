package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyh.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 站内通知实体
 *
 * @Author LiYH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("notification")
public class Notification extends BaseEntity {

    /**
     * 发送者ID (系统通知为空)
     */
    private Long fromUserId;

    /**
     * 接收者ID
     */
    private Long toUserId;

    /**
     * 通知类型 1:点赞帖子 2:点赞评论 3:评论帖子 4:回复评论 5:关注 6:系统
     */
    private Integer type;

    /**
     * 目标ID (帖子ID/评论ID/用户ID)
     */
    private Long targetId;

    /**
     * 目标标题摘要
     */
    private String targetTitle;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 是否已读 0:未读 1:已读
     */
    private Integer isRead;

    /**
     * 发送者用户名 (非数据库字段)
     */
    @TableField(exist = false)
    private String fromUsername;

    /**
     * 发送者头像 (非数据库字段)
     */
    @TableField(exist = false)
    private String fromUserAvatar;
}
