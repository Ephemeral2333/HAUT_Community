package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liyh.model.entity.Billboard;
import com.liyh.system.service.BillBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.liyh.common.result.Result;

import java.util.List;

/**
 * @Author LiYH
 * @Description 公告牌controller
 * @Date 2023/6/5 17:49
 **/
@Api(tags = "公告牌接口")
@RestController
@RequestMapping("/front/billboard")
public class BillBoardController {
    @Autowired
    private BillBoardService billBoardService;

    @ApiOperation("获取公告牌")
    @GetMapping("/show")
    public Result<Billboard> getNotices() {
        QueryWrapper<Billboard> wrapper = new QueryWrapper<>();
        wrapper.eq("is_deleted", 0);
        List<Billboard> billboardList = billBoardService.list(wrapper);
        return Result.ok(billboardList.get(billboardList.size() - 1));
    }
}
