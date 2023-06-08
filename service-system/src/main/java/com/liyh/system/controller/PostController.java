package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.PostVo;
import com.liyh.system.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 帖子管理
 * @Date 2023/6/6 20:13
 **/
@Api(tags = "帖子管理")
@RestController
public class PostController {
    @Autowired
    private PostService postService;

    @ApiOperation("分页查询帖子")
    @PostMapping("/front/post/list/{tab}")
    public Result getPageList(@RequestBody Pagination pagination, @PathVariable String tab) {
        Page<Post> tip = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
//        IPage<Post> page = postService.selectPage(tip);
        IPage<Post> page;
        if ("hot".equals(tab)) {
            page = postService.selectPageByHot(tip);
        } else {
            page = postService.selectPageByTime(tip);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getRecords());
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("currentPage", page.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("发布帖子")
    @PostMapping("/front/post/save")
    public Result save(@RequestBody PostVo postVo, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        postService.savePost(postVo, userId);
        return Result.ok();
    }

    @ApiOperation("获取帖子详情")
    @GetMapping("/front/post/{id}")
    public Result<Post> getTopic(@PathVariable Long id) {
        Post post = postService.selectByPk(id);
        return Result.ok(post);
    }
}
