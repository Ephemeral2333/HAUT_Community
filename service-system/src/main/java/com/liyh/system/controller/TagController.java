package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.liyh.common.result.Result;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.Pagination;
import com.liyh.system.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 标签管理
 * @Date 2023/6/18 15:59
 **/
@Api(tags = "标签管理")
@RestController
@RequestMapping("/admin/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    @ApiOperation("分页查询标签")
    @PostMapping("/list")
    public Result list(@RequestBody Pagination pagination) {
        Page<Tag> tagPage = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Tag> page = tagService.selectTagsList(tagPage);
        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getRecords());
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("currentPage", page.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("新增标签")
    @PostMapping("/save")
    public Result save(@RequestBody JsonNode jsonNode) {
        String name = jsonNode.get("name").asText();
        tagService.saveTag(name);
        return Result.ok();
    }

    @ApiOperation("修改标签")
    @PutMapping("/update/{id}")
    public Result update(@RequestBody JsonNode jsonNode, @PathVariable("id") Long id) {
        String name = jsonNode.get("name").asText();
        tagService.updateTag(id, name);
        return Result.ok();
    }

    @ApiOperation("删除标签")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id) {
        tagService.removeById(id);
        tagService.removePostTagByTagId(id);
        return Result.ok();
    }
}
