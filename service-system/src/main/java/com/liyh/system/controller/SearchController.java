package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.model.vo.ai.SearchResultVo;
import com.liyh.system.service.EsPostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器 - ES 全文检索 + 向量语义搜索
 */
@Api(tags = "搜索管理")
@RestController
@RequestMapping("/front/search")
@Slf4j
public class SearchController {

    @Autowired
    private EsPostService esPostService;

    @ApiOperation("混合搜索（BM25 + 向量语义，RRF 融合排序）")
    @GetMapping("/hybrid")
    public Result<?> hybridSearch(@RequestParam String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.fail("搜索关键词不能为空");
        }

        if (!esPostService.isAvailable()) {
            return Result.fail("搜索服务暂不可用");
        }

        List<SearchResultVo> results = esPostService.hybridSearch(keyword.trim(), page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("list", results);
        data.put("keyword", keyword);
        data.put("page", page);
        data.put("size", size);
        return Result.ok(data);
    }

    @ApiOperation("关键词搜索（BM25）")
    @GetMapping("/keyword")
    public Result<?> keywordSearch(@RequestParam String keyword,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.fail("搜索关键词不能为空");
        }

        if (!esPostService.isAvailable()) {
            return Result.fail("搜索服务暂不可用");
        }

        List<SearchResultVo> results = esPostService.searchByKeyword(keyword.trim(), page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("list", results);
        data.put("keyword", keyword);
        data.put("page", page);
        data.put("size", size);
        return Result.ok(data);
    }

    @ApiOperation("全量重建索引（管理员操作：将数据库所有帖子导入 ES）")
    @PostMapping("/reindex")
    public Result<?> reindex() {
        if (!esPostService.isAvailable()) {
            return Result.fail("ES 服务不可用");
        }
        int count = esPostService.reindexAll();
        return Result.ok("全量索引完成，成功索引 " + count + " 篇帖子");
    }

    @ApiOperation("语义搜索（向量相似度）")
    @GetMapping("/semantic")
    public Result<?> semanticSearch(@RequestParam String query,
                                    @RequestParam(defaultValue = "10") int topK) {
        if (query == null || query.trim().isEmpty()) {
            return Result.fail("搜索内容不能为空");
        }

        if (!esPostService.isAvailable()) {
            return Result.fail("搜索服务暂不可用");
        }

        List<SearchResultVo> results = esPostService.searchByVector(query.trim(), topK);

        Map<String, Object> data = new HashMap<>();
        data.put("list", results);
        data.put("query", query);
        return Result.ok(data);
    }
}
