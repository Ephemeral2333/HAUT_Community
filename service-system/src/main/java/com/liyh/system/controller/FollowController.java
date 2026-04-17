package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.vo.FollowerVo;
import com.liyh.model.vo.UserVo;
import com.liyh.system.service.FollowService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 关注控制器
 * @Date 2023/6/7 22:40
 **/
@Tag(name = "关注控制器")
@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @Operation(summary = "关注")
    @PostMapping("/front/follow/{id}")
    public Result follow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId.equals(String.valueOf(id))) {
            return Result.ok("不可以关注自己");
        }
        followService.follow(userId, id);
        return Result.ok();
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/front/unfollow/{id}")
    public Result unfollow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        followService.unfollow(userId, id);
        return Result.ok();
    }

    @Operation(summary = "判断是否关注对方")
    @GetMapping("/front/isFollow/{id}")
    public Result isFollow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Map<String, Object> map = new HashMap<>();
        map.put("hasFollow", followService.isFollow(id, userId));
        return Result.ok(map);
    }

    @Operation(summary = "获取关注列表")
    @GetMapping("/front/follow/{page}/{limit}")
    public Result getFollowList(@PathVariable Long page,
                                @PathVariable Long limit,
                                @RequestParam(name = "username") String username) {
        Page<FollowerVo> pageParam = new Page<>(page, limit);
        IPage<FollowerVo> pageModel = followService.getFollowList(pageParam, username);
        Map<String, Object> map = new HashMap<>();
        map.put("items", pageModel.getRecords());
        map.put("total", pageModel.getTotal());
        map.put("page", pageModel.getCurrent());
        map.put("size", pageModel.getSize());
        return Result.ok(map);
    }

    @Operation(summary = "获取粉丝列表")
    @GetMapping("/front/fans/{page}/{limit}")
    public Result getFansList(@PathVariable Long page,
                              @PathVariable Long limit,
                              @RequestParam(name = "username") String username) {
        Page<FollowerVo> pageParam = new Page<>(page, limit);
        IPage<FollowerVo> pageModel = followService.getFansList(pageParam, username);
        Map<String, Object> map = new HashMap<>();
        map.put("items", pageModel.getRecords());
        map.put("total", pageModel.getTotal());
        map.put("page", pageModel.getCurrent());
        map.put("size", pageModel.getSize());
        return Result.ok(map);
    }
}
