package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import com.liyh.model.vo.Pagination;
import com.liyh.system.service.TipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 每日一句控制器
 * @Date 2023/6/5 22:02
 **/
@Api(tags = "每日一句接口")
@RestController
public class TipController {
    @Autowired
    private TipService tipService;

    @ApiOperation("随机获取每日一句")
    @GetMapping("/front/tip/today")
    public Result<Tip> getTip() {
        Tip tip = tipService.getRandomTip();
        return Result.ok(tip);
    }

    @ApiOperation("获取分页列表")
    @PostMapping("/admin/tip/getPageList")
    public Result getPageList(@RequestBody Pagination pagination) {
        Page<Tip> tip = new Page<>(pagination.getCurrentPage(), pagination.getPageSize());
        IPage<Tip> page = tipService.selectPage(tip);
        Map<String, Object> map = new HashMap<>();
        map.put("list", page.getRecords());
        map.put("total", page.getTotal());
        map.put("pageSize", page.getSize());
        map.put("currentPage", page.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation("删除每日一句")
    @DeleteMapping("/admin/tip/delete/{id}")
    public Result deleteTip(@PathVariable("id") Integer id) {
        tipService.removeById(id);
        return Result.ok();
    }

    @ApiOperation("新增每日一句")
    @PostMapping("/admin/tip/save")
    public Result saveTip(@RequestBody Tip tip) {
        tipService.save(tip);
        return Result.ok();
    }

    @ApiOperation("修改每日一句")
    @PutMapping("/admin/tip/update/{id}")
    public Result updateTip(@RequestBody Tip tip, @PathVariable Long id) {
        tip.setId(id);
        tipService.updateById(tip);
        return Result.ok();
    }
}
