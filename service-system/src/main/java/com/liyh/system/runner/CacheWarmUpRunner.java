package com.liyh.system.runner;

import com.liyh.common.constant.RedisConstant;
import com.liyh.model.entity.Post;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 缓存预热 - 应用启动时从数据库初始化 Redis 缓存
 *
 * @Author LiYH
 */
@Component
@Slf4j
public class CacheWarmUpRunner implements CommandLineRunner {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void run(String... args) {
        log.info("========== 开始缓存预热 ==========");
        long startTime = System.currentTimeMillis();

        try {
            // 1. 预热帖子浏览量
            warmUpViewCount();

            // 2. 预热帖子点赞数
            warmUpLikeCount();

            // 3. 预热帖子收藏数
            warmUpCollectCount();

            // 4. 初始化热帖排行榜
            warmUpHotPosts();

            long endTime = System.currentTimeMillis();
            log.info("========== 缓存预热完成，耗时: {}ms ==========", endTime - startTime);
        } catch (Exception e) {
            log.error("缓存预热失败", e);
        }
    }

    /**
     * 预热帖子浏览量
     */
    private void warmUpViewCount() {
        log.info("预热帖子浏览量...");
        List<Post> posts = postMapper.selectList(null);
        int count = 0;
        for (Post post : posts) {
            if (post.getIsDeleted() == 0) {
                String key = RedisConstant.POST_VIEW_COUNT + post.getId();
                redisUtil.set(key, post.getView());
                count++;
            }
        }
        log.info("预热帖子浏览量完成，共 {} 条", count);
    }

    /**
     * 预热帖子点赞数
     */
    private void warmUpLikeCount() {
        log.info("预热帖子点赞数...");
        List<Post> posts = postMapper.selectList(null);
        int count = 0;
        for (Post post : posts) {
            if (post.getIsDeleted() == 0) {
                int likeCount = postMapper.getFavoriteCountByPostId(post.getId());
                String key = RedisConstant.POST_LIKE_COUNT + post.getId();
                redisUtil.set(key, likeCount);
                count++;
            }
        }
        log.info("预热帖子点赞数完成，共 {} 条", count);
    }

    /**
     * 预热帖子收藏数
     */
    private void warmUpCollectCount() {
        log.info("预热帖子收藏数...");
        List<Post> posts = postMapper.selectList(null);
        int count = 0;
        for (Post post : posts) {
            if (post.getIsDeleted() == 0) {
                int collectCount = postMapper.getCollectsCountByPostId(post.getId());
                String key = RedisConstant.POST_COLLECT_COUNT + post.getId();
                redisUtil.set(key, collectCount);
                count++;
            }
        }
        log.info("预热帖子收藏数完成，共 {} 条", count);
    }

    /**
     * 初始化热帖排行榜
     */
    private void warmUpHotPosts() {
        log.info("初始化热帖排行榜...");
        List<Post> posts = postMapper.selectList(null);
        int count = 0;
        for (Post post : posts) {
            if (post.getIsDeleted() == 0) {
                // 计算热度分数：浏览*1 + 点赞*3 + 收藏*5
                int likeCount = postMapper.getFavoriteCountByPostId(post.getId());
                int collectCount = postMapper.getCollectsCountByPostId(post.getId());
                
                double hotScore = post.getView() * RedisConstant.VIEW_SCORE
                        + likeCount * RedisConstant.LIKE_SCORE
                        + collectCount * RedisConstant.COLLECT_SCORE;

                redisUtil.zsetAdd(RedisConstant.HOT_POSTS_DAILY, post.getId(), hotScore);
                count++;
            }
        }
        log.info("初始化热帖排行榜完成，共 {} 条", count);
    }
}
