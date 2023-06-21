package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tip;
import com.liyh.model.vo.PostVo;

import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子接口
 * @Date 2023/6/5 17:46
 **/

public interface PostService extends IService<Post> {
    // 分页查询热门帖子
    IPage<Post> selectPageByHot(Page<Post> tip);

    // 分页查询最新帖子
    IPage<Post> selectPageByTime(Page<Post> tip);

    // 发布帖子
    Post savePost(PostVo postVo, String userId);

    // 获取帖子详情
    Post selectByPk(Long id);

    // 分页查询我的帖子
    IPage<Post> selectPageByUserId(Page<Post> page, String userId);

    // 随机给出帖子
    List<Post> selectPostRandom();

    // 更新帖子
    Post updatePost(PostVo postVo, String userId);

    // 分页查询所有帖子
    IPage<Post> selectAllPage(Page<Post> page);

    // 增加浏览量
    void increaseViewCount(Long id);

    // 分页查询某个标签的所有帖子
    IPage<Post> selectPageByTagId(Page<Post> postPage, Long id);

    // 通过关键词搜索帖子
    IPage<Post> searchByKeyword(Page<Post> page, String keyWord);

    // 删除帖子
    void deletePost(Long id);

    List<Post> selectRandomPostByLike(String userId);

    List<Post> selectRandomPostByMy(String userId);

    void favor(String userId, Long id);

    void unfavor(String userId, Long id);

    boolean isFavor(String userId, Long id);

    void increaseShareCount(Long id);

    IPage<Post> selectPageByCollectUserId(Page<Post> page, String userId);

    IPage<Post> selectPageByLikeUserId(Page<Post> page, String userId);

    boolean isCollect(String userId, Long id);

    void collect(String userId, Long id);
}
