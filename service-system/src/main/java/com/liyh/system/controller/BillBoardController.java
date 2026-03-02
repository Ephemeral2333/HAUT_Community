package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Billboard;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.Pagination;
import com.liyh.system.mq.producer.MessageProducer;
import com.liyh.system.service.BillBoardService;
import com.liyh.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.liyh.common.result.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 公告牌controller
 * @Date 2023/6/5 17:49
 **/
@Api(tags = "公告牌接口")
@RestController
@Slf4j
public class BillBoardController {
    @Autowired
    private BillBoardService billBoardService;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("获取公告牌")
    @GetMapping("/front/billboard/show")
    public Result<Billboard> getNotices() {
        Billboard billboard = billBoardService.selectOrderByTime();
        return Result.ok(billboard);
    }

    @ApiOperation("获取分页列表")
    @PostMapping("/admin/billboard/getPageList")
    public Result getPageList(@RequestBody Pagination pagination) {
        Page<Billboard> billboards = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Billboard> page = billBoardService.selectPageList(billboards);
        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getRecords());
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("currentPage", page.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("添加公告牌")
    @PostMapping("/admin/billboard/save")
    public Result addBillBoard(@RequestBody JsonNode jsonNode) {
        String content = jsonNode.get("content").asText();
        billBoardService.insertBillBoard(content);
        return Result.ok();
    }

    @ApiOperation("修改公告牌")
    @PutMapping("/admin/billboard/update/{id}")
    public Result updateBillBoard(@RequestBody JsonNode jsonNode, @PathVariable("id") Long id) {
        String content = jsonNode.get("content").asText();
        billBoardService.updateBillBoard(id, content);
        return Result.ok();
    }

    @ApiOperation("删除公告牌")
    @DeleteMapping("/admin/billboard/delete/{id}")
    public Result deleteBillBoard(@PathVariable("id") Long id) {
        billBoardService.deleteBillBoard(id);
        return Result.ok();
    }

    // ==================== 广播公告功能 ====================

    @ApiOperation("广播系统公告（给所有用户发送通知）")
    @PostMapping("/admin/billboard/broadcast")
    public Result broadcastAnnouncement(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        
        String title = jsonNode.has("title") ? jsonNode.get("title").asText() : "系统公告";
        String content = jsonNode.get("content").asText();
        
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("公告内容不能为空");
        }
        
        // 先保存公告到数据库
        billBoardService.insertBillBoard(content);
        
        // 获取发送者信息
        SysUser sender = sysUserService.getById(Long.parseLong(userId));
        String senderName = sender != null ? sender.getUsername() : "系统管理员";
        
        // 获取刚保存的公告ID
        Billboard billboard = billBoardService.selectOrderByTime();
        Long billboardId = billboard != null ? billboard.getId() : null;
        
        // 发送广播消息到MQ
        messageProducer.broadcastAnnouncement(
                title,
                content,
                billboardId,
                Long.parseLong(userId),
                senderName
        );
        
        log.info("管理员{}发起广播公告: {}", userId, title);
        
        Map<String, Object> result = new HashMap<>();
        result.put("billboardId", billboardId);
        result.put("message", "公告已发送，正在广播给所有用户");
        
        return Result.ok(result);
    }

    @ApiOperation("广播系统维护通知")
    @PostMapping("/admin/billboard/broadcast/maintenance")
    public Result broadcastMaintenance(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        
        String title = jsonNode.has("title") ? jsonNode.get("title").asText() : "系统维护通知";
        String content = jsonNode.get("content").asText();
        
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("通知内容不能为空");
        }
        
        // 获取发送者信息
        SysUser sender = sysUserService.getById(Long.parseLong(userId));
        String senderName = sender != null ? sender.getUsername() : "系统管理员";
        
        // 发送维护通知
        messageProducer.broadcastMaintenance(
                title,
                content,
                Long.parseLong(userId),
                senderName
        );
        
        log.info("管理员{}发起系统维护通知: {}", userId, title);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "维护通知已广播");
        
        return Result.ok(result);
    }

    @ApiOperation("广播活动通知")
    @PostMapping("/admin/billboard/broadcast/activity")
    public Result broadcastActivity(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        
        String title = jsonNode.has("title") ? jsonNode.get("title").asText() : "活动通知";
        String content = jsonNode.get("content").asText();
        Long targetId = jsonNode.has("targetId") ? jsonNode.get("targetId").asLong() : null;
        
        if (content == null || content.trim().isEmpty()) {
            return Result.fail("活动内容不能为空");
        }
        
        // 获取发送者信息
        SysUser sender = sysUserService.getById(Long.parseLong(userId));
        String senderName = sender != null ? sender.getUsername() : "系统管理员";
        
        // 发送活动通知
        messageProducer.broadcastActivity(
                title,
                content,
                targetId,
                Long.parseLong(userId),
                senderName
        );
        
        log.info("管理员{}发起活动通知: {}", userId, title);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "活动通知已广播");
        
        return Result.ok(result);
    }
}
