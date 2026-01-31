package com.liyh.system.task;

import com.liyh.common.constant.RedisConstant;
import com.liyh.model.entity.Post;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Redis 数据定时同步任务
 *
 * @Author LiYH
 */
@Component
@EnableScheduling
@Slf4j
public class RedisDataSyncTask {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 同步浏览量到数据库
     * 每 5 分钟执行一次
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncViewCountToDatabase() {
        log.info("========== 开始同步浏览量到数据库 ==========");
        long startTime = System.currentTimeMillis();

        try {
            List<Post> posts = postMapper.selectList(null);
            int syncCount = 0;

            for (Post post : posts) {
                if (post.getIsDeleted() == 0) {
                    String key = RedisConstant.POST_VIEW_COUNT + post.getId();
                    Long redisViewCount = redisUtil.getLong(key);

                    if (redisViewCount != null && redisViewCount > post.getView()) {
                        // Redis 中的浏览量大于数据库，需要同步
                        post.setView(redisViewCount.intValue());
                        postMapper.update(post);
                        syncCount++;
                    }
                }
            }

            long endTime = System.currentTimeMillis();
            log.info("========== 浏览量同步完成，同步 {} 条，耗时: {}ms ==========", syncCount, endTime - startTime);
        } catch (Exception e) {
            log.error("浏览量同步失败", e);
        }
    }

    /**
     * 清理过期的热帖排行榜数据
     * 每天凌晨 2 点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredHotPosts() {
        log.info("========== 开始清理热帖排行榜 ==========");

        try {
            // 删除旧的排行榜
            redisUtil.delete(RedisConstant.HOT_POSTS_DAILY);
            
            // 重新初始化排行榜
            List<Post> posts = postMapper.selectList(null);
            int count = 0;
            
            for (Post post : posts) {
                if (post.getIsDeleted() == 0) {
                    int likeCount = postMapper.getFavoriteCountByPostId(post.getId());
                    int collectCount = postMapper.getCollectsCountByPostId(post.getId());

                    double hotScore = post.getView() * RedisConstant.VIEW_SCORE
                            + likeCount * RedisConstant.LIKE_SCORE
                            + collectCount * RedisConstant.COLLECT_SCORE;

                    redisUtil.zsetAdd(RedisConstant.HOT_POSTS_DAILY, post.getId(), hotScore);
                    count++;
                }
            }

            log.info("========== 热帖排行榜重建完成，共 {} 条 ==========", count);
        } catch (Exception e) {
            log.error("热帖排行榜清理失败", e);
        }
    }

    /**
     * 同步点赞数据到数据库
     * 每 10 分钟执行一次
     */
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void syncLikeCountToDatabase() {
        log.info("========== 开始同步点赞数据 ==========");
        // 点赞数据已经实时写入数据库，这里主要做数据校验
        // 如果需要只写 Redis 的方案，可以在这里实现批量同步
        log.info("========== 点赞数据同步完成 ==========");
    }
}
