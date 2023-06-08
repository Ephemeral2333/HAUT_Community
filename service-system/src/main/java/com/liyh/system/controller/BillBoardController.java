package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.liyh.model.entity.Billboard;
import com.liyh.model.vo.Pagination;
import com.liyh.system.service.BillBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.liyh.common.result.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 公告牌controller
 * @Date 2023/6/5 17:49
 **/
@Api(tags = "公告牌接口")
@RestController
public class BillBoardController {
    @Autowired
    private BillBoardService billBoardService;

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
}
