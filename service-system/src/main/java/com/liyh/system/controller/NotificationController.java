package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.vo.NotificationVo;
import com.liyh.model.vo.Pagination;
import com.liyh.system.service.NotificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 站内通知控制器
 *
 * @Author LiYH
 */
@Api(tags = "站内通知")
@RestController
@RequestMapping("/front/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页获取通知列表
     */
    @ApiOperation("获取通知列表")
    @PostMapping("/list")
    public Result getList(@RequestBody Pagination pagination,
                          @RequestParam(required = false) String types,
                          HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        Page<NotificationVo> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<NotificationVo> result = notificationService.getPageByUserId(page, Long.parseLong(userId), types);

        Map<String, Object> map = new HashMap<>();
        map.put("list", result.getRecords());
        map.put("total", result.getTotal());
        return Result.ok(map);
    }

    /**
     * 获取未读通知数量
     */
    @ApiOperation("获取未读通知数量")
    @GetMapping("/unread/count")
    public Result getUnreadCount(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        Map<String, Integer> countMap = notificationService.getUnreadCountByType(Long.parseLong(userId));
        return Result.ok(countMap);
    }

    /**
     * 标记单条通知为已读
     */
    @ApiOperation("标记单条为已读")
    @PutMapping("/read/{id}")
    public Result markAsRead(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        notificationService.markAsRead(id, Long.parseLong(userId));
        return Result.ok();
    }

    /**
     * 标记全部通知为已读
     */
    @ApiOperation("标记全部已读")
    @PutMapping("/read/all")
    public Result markAllAsRead(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        notificationService.markAllAsRead(Long.parseLong(userId));
        return Result.ok();
    }

    /**
     * 按类型标记通知为已读
     */
    @ApiOperation("按类型标记已读")
    @PutMapping("/read/type/{type}")
    public Result markAsReadByType(@PathVariable Integer type, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        notificationService.markAsReadByType(Long.parseLong(userId), type);
        return Result.ok();
    }

    /**
     * 删除通知
     */
    @ApiOperation("删除通知")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));

        notificationService.delete(id, Long.parseLong(userId));
        return Result.ok();
    }
}
