package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.system.service.FollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 关注控制器
 * @Date 2023/6/7 22:40
 **/
@Api(tags = "关注控制器")
@RestController
public class FollowController {
    @Autowired
    private FollowService followService;

    @ApiOperation("关注")
    @PostMapping("/front/follow/{id}")
    public Result follow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        followService.follow(userId, id);
        return Result.ok();
    }

    @ApiOperation("取消关注")
    @PostMapping("/front/unfollow/{id}")
    public Result unfollow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        followService.unfollow(userId, id);
        return Result.ok();
    }

    @ApiOperation("判断是否关注对方")
    @GetMapping("/front/isFollow/{id}")
    public Result isFollow(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Map<String, Object> map = new HashMap<>();
        map.put("hasFollow", followService.isFollow(id, userId));
        return Result.ok(map);
    }
}
