package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.vo.CommentPostVo;
import com.liyh.system.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author LiYH
 * @Description 评论管理
 * @Date 2023/6/16 19:57
 **/
@Api(tags = "评论管理")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "获取某话题评论列表")
    @GetMapping("/front/comment/getComments/{id}")
    public Result getTopicCommentList(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId == null) {
            return Result.ok(commentService.getTopicCommentListByTopicId(id, null));
        } else {
            return Result.ok(commentService.getTopicCommentListByTopicId(id, Long.valueOf(userId)));
        }
    }

    @ApiOperation(value = "评论操作")
    @PostMapping("/admin/comments/pushComments")
    public Result pushComments(@RequestBody CommentPostVo commentPostVo, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId != null) {
            commentService.pushComments(commentPostVo, Long.valueOf(userId));
        } else {
            return Result.fail("请先登录");
        }
        return Result.ok();
    }

    @ApiOperation(value = "点赞操作")
    @PostMapping("/admin/comments/favor/{id}")
    public Result favor(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId != null) {
            commentService.favor(id, Long.valueOf(userId));
        } else {
            return Result.fail("请先登录");
        }
        return Result.ok();
    }

    @ApiOperation("对评论进行回复")
    @PostMapping("/admin/comments/reply")
    public Result replyComment(@RequestBody CommentPostVo commentPostVo, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId != null) {
            commentService.replyComment(commentPostVo, Long.valueOf(userId));
        } else {
            return Result.fail("请先登录");
        }
        return Result.ok();
    }

    @ApiOperation("删除评论")
    @DeleteMapping("/admin/comments/delete/{id}")
    public Result deleteComment(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.ok();
    }
}
