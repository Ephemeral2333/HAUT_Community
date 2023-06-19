package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.PostVo;
import com.liyh.system.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/18 16:26
 **/
@Api(tags = "搜索管理")
@RestController
@RequestMapping("/front")
public class SearchController {
    @Autowired
    private PostService postService;

    @ApiOperation(value = "搜索")
    @GetMapping("/search")
    public Result search(@RequestParam("keyword") String keyWord,
                         @RequestParam("pageNum") Integer pageNum,
                         @RequestParam("pageSize") Integer pageSize) {
        Page<Post> page = new Page<>(pageNum, pageSize);
        IPage<Post> postVoIPage = postService.searchByKeyword(page, keyWord);
        Map<String, Object> map = new HashMap<>();
        map.put("records", postVoIPage.getRecords());
        map.put("total", postVoIPage.getTotal());
        map.put("size", postVoIPage.getSize());
        map.put("current", postVoIPage.getCurrent());
        return Result.ok(postVoIPage);
    }
}
