package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Notification;
import com.liyh.model.vo.NotificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知 Mapper
 *
 * @Author LiYH
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 分页查询用户通知列表 (关联用户表获取发送者信息)
     *
     * @param page     分页参数
     * @param userId   用户ID
     * @param typeList 通知类型列表 (可选，支持多类型)
     * @return 通知分页列表
     */
    IPage<NotificationVo> selectPageByUserId(@Param("page") Page<NotificationVo> page,
                                              @Param("userId") Long userId,
                                              @Param("typeList") List<Integer> typeList);

    /**
     * 查询用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    Integer selectUnreadCount(@Param("userId") Long userId);

    /**
     * 按类型查询用户未读通知数量
     *
     * @param userId 用户ID
     * @param type   通知类型
     * @return 未读数量
     */
    Integer selectUnreadCountByType(@Param("userId") Long userId, @Param("type") Integer type);

    /**
     * 标记所有通知为已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(@Param("userId") Long userId);

    /**
     * 按类型标记通知为已读
     *
     * @param userId 用户ID
     * @param type   通知类型
     */
    void markAsReadByType(@Param("userId") Long userId, @Param("type") Integer type);
}
