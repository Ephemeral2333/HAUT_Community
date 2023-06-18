package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.PostVo;
import com.liyh.system.service.PostService;
import com.liyh.system.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 帖子管理
 * @Date 2023/6/6 20:13
 **/
@Api(tags = "帖子管理")
@RestController
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private TagService tagService;

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
        Post post = postService.savePost(postVo, userId);
        return Result.ok(post);
    }

    @ApiOperation("修改帖子")
    @PutMapping("/front/post/update")
    public Result update(@RequestBody PostVo postVo, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Post post = postService.updatePost(postVo, userId);
        return Result.ok(post);
    }

    @ApiOperation("获取帖子详情")
    @GetMapping("/front/post/{id}")
    public Result<Post> getTopic(@PathVariable Long id) {
        postService.increaseViewCount(id);  // 增加浏览量
        Post post = postService.selectByPk(id);
        return Result.ok(post);
    }

    @ApiOperation("获取我的帖子")
    @PostMapping("/front/post/my")
    public Result getMyPost(@RequestBody Pagination pagination, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Page<Post> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Post> iPage = postService.selectPageByUserId(page, userId);
        Map<String, Object> map = new HashMap<>();
        map.put("list", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("pageSize", iPage.getSize());
        map.put("currentPage", iPage.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("随机获取十个帖子")
    @GetMapping("/front/post/recommend")
    public Result getPostRandom() {
        return Result.ok(postService.selectPostRandom());
    }

    @ApiOperation("删除帖子")
    @DeleteMapping("/front/post/delete/{id}")
    public Result deletePost(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Post post = postService.selectByPk(id);
        log.info("post = " + post.getAuthor().getId());
        log.info("userId = " + userId);
        if (userId != null && !userId.equals(String.valueOf(post.getAuthor().getId()))) {
            return Result.ok("无权限删除");
        }
        postService.removeById(id);
        System.out.println("post = " + post.getUserId());
        return Result.ok();
    }

    @ApiOperation("获取所有帖子")
    @PostMapping("/admin/post/getPageList")
    public Result getAllPost(@RequestBody Pagination pagination) {
        Page<Post> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Post> iPage = postService.selectAllPage(page);
        Map<String, Object> map = new HashMap<>();
        map.put("list", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("pageSize", iPage.getSize());
        map.put("currentPage", iPage.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("获取某标签下的帖子")
    @PostMapping("/front/tag/getPageList/{id}")
    public Result getPostByLabelId(@PathVariable Long id,
                                   @RequestParam Integer page,
                                   @RequestParam Integer size) {
        Page<Post> postPage = new Page<>(page, size);
        IPage<Post> iPage = postService.selectPageByTagId(postPage, id);

        Map<String, Object> map = new HashMap<>();
        map.put("topics", iPage.getRecords());
        map.put("size", iPage.getSize());
        map.put("page", iPage.getCurrent());
        map.put("tags", tagService.getHotTags());
        map.put("nowTag", tagService.getNameById(id));
        return Result.ok(map);
    }
}
