package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.FollowerVo;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.PostVo;
import com.liyh.model.vo.UserVo;
import com.liyh.system.service.FollowService;
import com.liyh.system.service.PostService;
import com.liyh.system.service.SysUserService;
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

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation("分页查询帖子")
    @PostMapping("/front/post/list/{tab}")
    public Result getPageList(@RequestBody Pagination pagination, @PathVariable String tab) {
        Page<Post> tip = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
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
        if (userId != null && !userId.equals(String.valueOf(post.getAuthor().getId()))) {
            return Result.ok("无权限删除");
        }
        postService.deletePost(id);
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

    @ApiOperation("获取用户主页的帖子")
    @GetMapping("/front/user/info/{username}")
    public Result getPostByUserId(@PathVariable String username,
                                  @RequestParam Integer page,
                                  @RequestParam Integer size) {
        SysUser sysUser = sysUserService.getByUsername(username);
        UserVo userVo = sysUserService.getUserInfo(sysUser.getId());
        Page<Post> postPage = new Page<>(page, size);
        IPage<Post> iPage = postService.selectPageByUserId(postPage, String.valueOf(sysUser.getId()));

        Map<String, Object> map = new HashMap<>();
        map.put("records", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("pageSize", iPage.getSize());
        map.put("currentPage", iPage.getCurrent());
        map.put("user", userVo);
        return Result.ok(map);
    }

    @ApiOperation("随机获取5个点赞的帖子")
    @GetMapping("/post/like/random")
    public Result getPostByLike(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        return Result.ok(postService.selectRandomPostByLike(userId));
    }

    @ApiOperation("随机获取我的5个帖子")
    @GetMapping("/post/my/random")
    public Result getPostByMy(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        return Result.ok(postService.selectRandomPostByMy(userId));
    }

    @ApiOperation("点赞帖子")
    @GetMapping("/post/favor/{id}")
    public Result favor(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        postService.favor(userId, id);
        return Result.ok();
    }

    @ApiOperation("判断是否点赞帖子")
    @GetMapping("/post/isFavor/{id}")
    public Result isFavor(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        return Result.ok(postService.isFavor(userId, id));
    }

    @ApiOperation("取消点赞帖子")
    @GetMapping("/post/unfavor/{id}")
    public Result unfavor(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        postService.unfavor(userId, id);
        return Result.ok();
    }

    @ApiOperation("增加转发量")
    @GetMapping("/post/increaseShareCount/{id}")
    public Result increaseShareCount(@PathVariable Long id) {
        postService.increaseShareCount(id);
        log.info("增加转发量成功");
        return Result.ok();
    }

    @ApiOperation("获取收藏帖子")
    @PostMapping("/post/my/{tab}")
    public Result getMyCollects(@RequestBody Pagination pagination, HttpServletRequest request,
                                @PathVariable String tab) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        Page<Post> page = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Post> iPage;
        if ("collect".equals(tab)) {
            iPage = postService.selectPageByCollectUserId(page, userId);
        } else {
            iPage = postService.selectPageByLikeUserId(page, userId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", iPage.getRecords());
        map.put("total", iPage.getTotal());
        map.put("pageSize", iPage.getSize());
        map.put("currentPage", iPage.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("判断是否收藏帖子")
    @GetMapping("/post/isCollect/{id}")
    public Result isCollect(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        return Result.ok(postService.isCollect(userId, id));
    }

    @ApiOperation("收藏或取消收藏")
    @GetMapping("/post/collect/{id}")
    public Result collect(@PathVariable Long id, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        postService.collect(userId, id);
        return Result.ok();
    }
}
