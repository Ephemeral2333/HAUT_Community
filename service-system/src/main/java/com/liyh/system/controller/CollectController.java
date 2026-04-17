package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.model.entity.Billboard;
import com.liyh.model.entity.Collect;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.Pagination;
import com.liyh.system.service.CollectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author LiYH
 * @Description 收藏controller
 * @Date 2023/6/21 8:51
 **/
@Tag(name = "收藏管理")
@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private CollectService collectService;
}
