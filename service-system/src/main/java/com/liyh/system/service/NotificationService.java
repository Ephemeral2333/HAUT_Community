package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Notification;
import com.liyh.model.vo.NotificationVo;
import com.liyh.model.vo.message.NotifyMessage;

import java.util.Map;

/**
 * 通知服务接口
 *
 * @Author LiYH
 */
public interface NotificationService extends IService<Notification> {

    /**
     * 从 MQ 消息保存通知
     *
     * @param message 通知消息
     */
    void saveFromMQ(NotifyMessage message);

    /**
     * 分页查询用户通知
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param types  通知类型，支持逗号分隔的多类型 (如 "1,2")
     * @return 通知分页列表
     */
    IPage<NotificationVo> getPageByUserId(Page<NotificationVo> page, Long userId, String types);

    /**
     * 获取未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 获取各类型未读通知数量
     *
     * @param userId 用户ID
     * @return 各类型未读数量 Map
     */
    Map<String, Integer> getUnreadCountByType(Long userId);

    /**
     * 标记单条通知为已读
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);

    /**
     * 按类型标记通知为已读
     *
     * @param userId 用户ID
     * @param type   通知类型
     */
    void markAsReadByType(Long userId, Integer type);

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void delete(Long notificationId, Long userId);
}
