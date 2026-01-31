package com.liyh.common.constant;

/**
 * Redis Key 常量
 *
 * @Author LiYH
 */
public class RedisConstant {

    // ==================== 帖子相关 ====================
    
    /**
     * 帖子点赞数 post:like:count:{postId}
     */
    public static final String POST_LIKE_COUNT = "post:like:count:";

    /**
     * 帖子收藏数 post:collect:count:{postId}
     */
    public static final String POST_COLLECT_COUNT = "post:collect:count:";

    /**
     * 帖子浏览量 post:view:count:{postId}
     */
    public static final String POST_VIEW_COUNT = "post:view:count:";

    /**
     * 帖子评论数 post:comment:count:{postId}
     */
    public static final String POST_COMMENT_COUNT = "post:comment:count:";

    // ==================== 用户行为相关 ====================
    
    /**
     * 用户点赞的帖子集合 user:liked:posts:{userId}
     */
    public static final String USER_LIKED_POSTS = "user:liked:posts:";

    /**
     * 用户收藏的帖子集合 user:collected:posts:{userId}
     */
    public static final String USER_COLLECTED_POSTS = "user:collected:posts:";

    /**
     * 用户点赞的评论集合 user:liked:comments:{userId}
     */
    public static final String USER_LIKED_COMMENTS = "user:liked:comments:";

    // ==================== 评论相关 ====================
    
    /**
     * 评论点赞数 comment:like:count:{commentId}
     */
    public static final String COMMENT_LIKE_COUNT = "comment:like:count:";

    // ==================== 排行榜相关 ====================
    
    /**
     * 每日热帖排行榜
     */
    public static final String HOT_POSTS_DAILY = "hot:posts:daily";

    /**
     * 每周热帖排行榜
     */
    public static final String HOT_POSTS_WEEKLY = "hot:posts:weekly";

    // ==================== 过期时间（秒） ====================
    
    /**
     * 帖子详情缓存过期时间：10分钟
     */
    public static final long POST_DETAIL_EXPIRE = 10 * 60;

    /**
     * 排行榜过期时间：1天
     */
    public static final long HOT_POSTS_EXPIRE = 24 * 60 * 60;

    /**
     * 计数缓存过期时间：7天
     */
    public static final long COUNT_EXPIRE = 7 * 24 * 60 * 60;

    // ==================== 热度权重 ====================
    
    /**
     * 点赞权重
     */
    public static final double LIKE_SCORE = 3.0;

    /**
     * 收藏权重
     */
    public static final double COLLECT_SCORE = 5.0;

    /**
     * 浏览权重
     */
    public static final double VIEW_SCORE = 1.0;

    /**
     * 评论权重
     */
    public static final double COMMENT_SCORE = 2.0;
}
