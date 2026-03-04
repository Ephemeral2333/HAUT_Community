package com.liyh.system.service;

import com.liyh.model.entity.Post;
import com.liyh.model.vo.ai.SearchResultVo;

import java.util.List;

/**
 * Elasticsearch 帖子搜索服务
 */
public interface EsPostService {

    /**
     * 索引帖子到 ES（含向量化）
     */
    void indexPost(Post post);

    /**
     * 从 ES 删除帖子
     */
    void deletePost(Long postId);

    /**
     * BM25 关键词搜索
     */
    List<SearchResultVo> searchByKeyword(String keyword, int page, int size);

    /**
     * 向量语义搜索
     */
    List<SearchResultVo> searchByVector(String query, int topK);

    /**
     * 混合搜索（BM25 + 向量，RRF 融合排序）
     */
    List<SearchResultVo> hybridSearch(String query, int page, int size);

    /**
     * 初始化 ES 索引（创建 mapping）
     */
    void initIndex();

    /**
     * 全量重建索引：将数据库中所有已发布帖子导入 ES
     *
     * @return 成功索引的帖子数量
     */
    int reindexAll();

    /**
     * ES 是否可用
     */
    boolean isAvailable();
}
