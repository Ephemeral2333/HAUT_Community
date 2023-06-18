package com.liyh.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.system.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/18 15:59
 **/
@Api(tags = "标签管理")
@RestController
public class TagController {
    @Autowired
    private TagService tagService;
}
