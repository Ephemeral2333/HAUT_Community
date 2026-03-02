package com.liyh.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 通知返回 VO
 *
 * @Author LiYH
 */
@Data
public class NotificationVo {

    /**
     * 通知ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Long fromUserId;

    /**
     * 发送者用户名
     */
    private String fromUsername;

    /**
     * 发送者头像
     */
    private String fromUserAvatar;

    /**
     * 通知类型 1:点赞帖子 2:点赞评论 3:评论帖子 4:回复评论 5:关注 6:系统
     */
    private Integer type;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 目标ID
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 相对时间 (5分钟前、1小时前等)
     */
    private String timeAgo;

    /**
     * 根据类型获取类型名称
     */
    public String getTypeName() {
        if (type == null) return "";
        return switch (type) {
            case 1 -> "点赞了你的帖子";
            case 2 -> "点赞了你的评论";
            case 3 -> "评论了你的帖子";
            case 4 -> "回复了你的评论";
            case 5 -> "关注了你";
            case 6 -> "系统通知";
            default -> "";
        };
    }

    /**
     * 计算相对时间
     */
    public String getTimeAgo() {
        if (createTime == null) return "";
        long diff = System.currentTimeMillis() - createTime.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "天前";
        if (hours > 0) return hours + "小时前";
        if (minutes > 0) return minutes + "分钟前";
        return "刚刚";
    }
}
