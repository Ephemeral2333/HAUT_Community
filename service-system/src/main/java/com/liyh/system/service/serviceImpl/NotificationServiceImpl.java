package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Notification;
import com.liyh.model.vo.NotificationVo;
import com.liyh.model.vo.message.NotifyMessage;
import com.liyh.system.mapper.NotificationMapper;
import com.liyh.system.service.FileService;
import com.liyh.system.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 *
 * @Author LiYH
 */
@Service
@Slf4j
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification>
        implements NotificationService {

    @Autowired
    private FileService fileService;

    @Override
    public void saveFromMQ(NotifyMessage message) {
        // 避免自己通知自己
        if (message.getFromUserId() != null && message.getFromUserId().equals(message.getToUserId())) {
            log.info("跳过自我通知: from={}, to={}", message.getFromUserId(), message.getToUserId());
            return;
        }

        Notification notification = Notification.builder()
                .fromUserId(message.getFromUserId())
                .toUserId(message.getToUserId())
                .type(convertType(message.getType()))
                .targetId(message.getTargetId())
                .targetTitle(message.getTargetTitle())
                .content(message.getContent())
                .isRead(0)
                .build();

        this.save(notification);
        log.info("通知保存成功: {} -> {}, type={}", message.getFromUsername(), message.getToUserId(), message.getType());
    }

    /**
     * 转换通知类型枚举为数据库整数
     */
    private Integer convertType(NotifyMessage.NotifyType type) {
        if (type == null)
            return 6; // 默认系统通知
        return switch (type) {
            case LIKE_POST -> 1;
            case LIKE_COMMENT -> 2;
            case COMMENT_POST -> 3;
            case REPLY_COMMENT -> 4;
            case FOLLOW -> 5;
            case SYSTEM -> 6;
        };
    }

    @Override
    public IPage<NotificationVo> getPageByUserId(Page<NotificationVo> page, Long userId, String types) {
        List<Integer> typeList = null;
        if (types != null && !types.isEmpty()) {
            typeList = Arrays.stream(types.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        IPage<NotificationVo> result = baseMapper.selectPageByUserId(page, userId, typeList);
        // 将通知中的 fromUserAvatar Key 拼接为完整 URL
        result.getRecords().forEach(vo -> vo.setFromUserAvatar(fileService.getFullUrl(vo.getFromUserAvatar())));
        return result;
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return baseMapper.selectUnreadCount(userId);
    }

    @Override
    public Map<String, Integer> getUnreadCountByType(Long userId) {
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("total", baseMapper.selectUnreadCount(userId));
        countMap.put("like",
                baseMapper.selectUnreadCountByType(userId, 1) + baseMapper.selectUnreadCountByType(userId, 2));
        countMap.put("comment",
                baseMapper.selectUnreadCountByType(userId, 3) + baseMapper.selectUnreadCountByType(userId, 4));
        countMap.put("follow", baseMapper.selectUnreadCountByType(userId, 5));
        countMap.put("system", baseMapper.selectUnreadCountByType(userId, 6));
        return countMap;
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        this.update(new UpdateWrapper<Notification>()
                .eq("id", notificationId)
                .eq("to_user_id", userId)
                .set("is_read", 1));
    }

    @Override
    public void markAllAsRead(Long userId) {
        baseMapper.markAllAsRead(userId);
    }

    @Override
    public void markAsReadByType(Long userId, Integer type) {
        baseMapper.markAsReadByType(userId, type);
    }

    @Override
    public void delete(Long notificationId, Long userId) {
        this.update(new UpdateWrapper<Notification>()
                .eq("id", notificationId)
                .eq("to_user_id", userId)
                .set("is_deleted", 1));
    }
}
