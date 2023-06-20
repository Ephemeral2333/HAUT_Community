package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.TipPostVo;
import com.liyh.system.service.TipPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 每日一句投稿控制器
 * @Date 2023/6/20 9:50
 **/
@Api(tags = "每日一句投稿管理")
@RestController
public class TipPostController {
    @Autowired
    private TipPostService tipPostService;

    @ApiOperation(value = "投稿")
    @PostMapping("/tip/post")
    public Result add(@RequestBody TipPostVo tipPostVo, HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        tipPostService.tipPost(tipPostVo, userId);
        return Result.ok();
    }

    @ApiOperation("获取分页列表")
    @PostMapping("/admin/tip/post/list")
    public Result getPageList(@RequestBody Pagination pagination) {
        Page<TipPost> tipPostPage = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<TipPost> page = tipPostService.selectPage(tipPostPage);
        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getRecords());
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("currentPage", page.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("通过投稿")
    @GetMapping("/admin/tip/pass/{id}")
    public Result pass(@PathVariable("id") Long id) {
        tipPostService.pass(id);
        return Result.ok();
    }

    @ApiOperation("拒绝投稿")
    @GetMapping("/admin/tip/refuse/{id}")
    public Result refuse(@PathVariable("id") Long id) {
        tipPostService.refuse(id);
        return Result.ok();
    }
}
