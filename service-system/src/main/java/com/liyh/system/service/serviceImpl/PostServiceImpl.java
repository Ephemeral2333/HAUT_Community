package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.common.constant.RedisConstant;
import com.liyh.system.utils.RedisUtil;
import com.liyh.model.entity.Collect;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.PostVo;
import com.liyh.system.mapper.CollectMapper;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.CommentService;
import com.liyh.system.service.FileService;
import com.liyh.system.service.PostService;
import com.liyh.system.service.SysUserService;
import com.liyh.system.service.TagService;
import com.liyh.system.mq.producer.MessageProducer;
import com.liyh.model.system.SysUser;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author LiYH
 * @Description 帖子service实现类
 * @Date 2023/6/5 17:47
 **/
@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private FileService fileService;

    /**
     * 将帖子作者的 headUrl Key 拼接为完整 URL
     */
    private void spliceAuthorAvatar(Post post) {
        if (post != null && post.getAuthor() != null) {
            post.getAuthor().setHeadUrl(fileService.getFullUrl(post.getAuthor().getHeadUrl()));
        }
    }

    private void spliceAuthorAvatarPage(IPage<Post> page) {
        if (page != null) {
            page.getRecords().forEach(this::spliceAuthorAvatar);
        }
    }

    @Override
    public IPage<Post> selectPageByHot(Page<Post> tip) {
        IPage<Post> result = postMapper.selectPageByHot(tip);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public IPage<Post> selectPageByTime(Page<Post> tip) {
        IPage<Post> result = postMapper.selectPageByTime(tip);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Post savePost(PostVo postVo, String userId) {
        Post post = Post.builder()
                .title(postVo.getTitle())
                .content(EmojiParser.parseToAliases(postVo.getContent()))
                .userId(Long.valueOf(userId))
                .anonymous(postVo.isAnonymous())
                .build();
        postMapper.insert(post);

        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
        }

        // 事务提交后再发 MQ 消息，避免消费者查不到未提交的数据
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    messageProducer.sendAuditMessage(post.getId());
                } catch (Exception e) {
                    log.warn("发送审核消息失败，帖子直接放行: {}", e.getMessage());
                }
                try {
                    messageProducer.sendEsIndexMessage(post.getId(), "index");
                } catch (Exception e) {
                    log.warn("发送 ES 索引消息失败: {}", e.getMessage());
                }
            }
        });

        return post;
    }

    @Override
    public Post selectByPk(Long id) {
        Post post = postMapper.selectByPk(id);
        spliceAuthorAvatar(post);
        return post;
    }

    @Override
    public IPage<Post> selectPageByUserId(Page<Post> page, String userId) {
        IPage<Post> result = postMapper.selectPageByUserId(page, userId);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public List<Post> selectPostRandom() {
        return postMapper.selectPostRandom();
    }

    @Override
    public Post updatePost(PostVo postVo, String userId) {
        Post post = postMapper.selectByPk(postVo.getId());
        post.setTitle(postVo.getTitle());
        post.setContent(EmojiParser.parseToAliases(postVo.getContent()));
        post.setAnonymous(postVo.isAnonymous());

        postMapper.update(post);

        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
        }

        // 更新 ES 索引
        try {
            messageProducer.sendEsIndexMessage(post.getId(), "index");
        } catch (Exception e) {
            log.warn("发送 ES 索引更新消息失败: {}", e.getMessage());
        }

        return post;
    }

    @Override
    public IPage<Post> selectAllPage(Page<Post> page) {
        IPage<Post> result = postMapper.selectAllPage(page);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public IPage<Post> selectPageByTagId(Page<Post> postPage, Long id) {
        IPage<Post> result = postMapper.selectPageByTagId(postPage, id);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public IPage<Post> searchByKeyword(Page<Post> page, String keyWord) {
        IPage<Post> result = postMapper.searchByKeyword(page, keyWord);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public void deletePost(Long id) {
        // 删除帖子
        postMapper.deleteById(id);
        // 删除帖子和标签的关联关系
        tagService.deleteTopicTagByTopicId(id);
        // 删除帖子的所有评论
        commentService.deleteCommentByPostId(id);

        // 清除Redis缓存
        redisUtil.delete(RedisConstant.POST_LIKE_COUNT + id);
        redisUtil.delete(RedisConstant.POST_COLLECT_COUNT + id);
        redisUtil.delete(RedisConstant.POST_VIEW_COUNT + id);

        // 从 ES 删除
        try {
            messageProducer.sendEsIndexMessage(id, "delete");
        } catch (Exception e) {
            log.warn("发送 ES 索引删除消息失败: {}", e.getMessage());
        }
    }

    @Override
    public List<Post> selectRandomPostByLike(String userId) {
        return postMapper.selectRandomPostByLike(userId);
    }

    @Override
    public List<Post> selectRandomPostByMy(String userId) {
        return postMapper.selectRandomPostByMy(userId);
    }

    // ==================== Redis 优化：点赞功能 ====================

    /**
     * 点赞帖子（Redis + DB）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favor(String userId, Long postId) {
        String userLikedKey = RedisConstant.USER_LIKED_POSTS + userId;
        String postLikeCountKey = RedisConstant.POST_LIKE_COUNT + postId;

        // 1. 检查 Redis 是否已点赞（防重复）
        if (Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, postId))) {
            log.debug("用户{}已点赞帖子{}，忽略重复操作", userId, postId);
            return;
        }

        // 2. Redis 操作
        redisUtil.setAdd(userLikedKey, postId); // 添加到用户点赞集合
        redisUtil.increment(postLikeCountKey); // 点赞数 +1
        updatePostHotScore(postId, RedisConstant.LIKE_SCORE); // 更新热度

        // 3. 数据库操作（保证持久化）
        postMapper.favor(userId, postId);

        // 4. 发送点赞通知
        try {
            Post post = postMapper.selectById(postId);
            if (post != null && !post.getUserId().equals(userId)) {
                SysUser fromUser = sysUserService.getById(Long.parseLong(userId));
                if (fromUser != null) {
                    messageProducer.sendLikeNotify(
                            Long.parseLong(userId),
                            fromUser.getUsername(),
                            post.getUserId(),
                            postId,
                            post.getTitle());
                }
            }
        } catch (Exception e) {
            log.warn("发送点赞通知失败: {}", e.getMessage());
        }

        log.info("用户{}点赞帖子{}", userId, postId);
    }

    /**
     * 取消点赞（Redis + DB）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavor(String userId, Long postId) {
        String userLikedKey = RedisConstant.USER_LIKED_POSTS + userId;
        String postLikeCountKey = RedisConstant.POST_LIKE_COUNT + postId;

        // 1. 检查是否已点赞
        if (!Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, postId))) {
            log.debug("用户{}未点赞帖子{}，忽略取消操作", userId, postId);
            return;
        }

        // 2. Redis 操作
        redisUtil.setRemove(userLikedKey, postId); // 从集合移除
        redisUtil.decrement(postLikeCountKey); // 点赞数 -1
        updatePostHotScore(postId, -RedisConstant.LIKE_SCORE);

        // 3. 数据库操作
        postMapper.unfavor(userId, postId);

        log.info("用户{}取消点赞帖子{}", userId, postId);
    }

    /**
     * 判断用户是否点赞了帖子（从 Redis 读取，O(1)）
     */
    @Override
    public boolean isFavor(String userId, Long postId) {
        String key = RedisConstant.USER_LIKED_POSTS + userId;
        Boolean isMember = redisUtil.setIsMember(key, postId);

        // 缓存未命中，从数据库查询
        if (isMember == null) {
            boolean dbResult = postMapper.isFavor(userId, postId) > 0;
            if (dbResult) {
                redisUtil.setAdd(key, postId); // 写入缓存
            }
            return dbResult;
        }
        return isMember;
    }

    /**
     * 获取帖子点赞数（从 Redis 读取）
     */
    public Long getPostLikeCount(Long postId) {
        String key = RedisConstant.POST_LIKE_COUNT + postId;
        Long count = redisUtil.getLong(key);

        // 缓存未命中，从数据库查询
        if (count == null) {
            int dbCount = postMapper.getFavoriteCountByPostId(postId);
            redisUtil.set(key, dbCount, RedisConstant.COUNT_EXPIRE, TimeUnit.SECONDS);
            return (long) dbCount;
        }
        return count;
    }

    // ==================== Redis 优化：收藏功能 ====================

    /**
     * 收藏/取消收藏帖子（Redis + DB）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collect(String userId, Long postId) {
        String userCollectedKey = RedisConstant.USER_COLLECTED_POSTS + userId;
        String postCollectCountKey = RedisConstant.POST_COLLECT_COUNT + postId;

        boolean isCollected = Boolean.TRUE.equals(redisUtil.setIsMember(userCollectedKey, postId));

        if (isCollected) {
            // 取消收藏
            redisUtil.setRemove(userCollectedKey, postId);
            redisUtil.decrement(postCollectCountKey);
            updatePostHotScore(postId, -RedisConstant.COLLECT_SCORE);
            collectMapper.unCollect(userId, postId);
            log.info("用户{}取消收藏帖子{}", userId, postId);
        } else {
            // 收藏
            redisUtil.setAdd(userCollectedKey, postId);
            redisUtil.increment(postCollectCountKey);
            updatePostHotScore(postId, RedisConstant.COLLECT_SCORE);

            Collect collect = Collect.builder()
                    .userId(Long.valueOf(userId))
                    .topicId(postId)
                    .build();
            collectMapper.insert(collect);
            log.info("用户{}收藏帖子{}", userId, postId);
        }
    }

    /**
     * 判断是否收藏（从 Redis 读取）
     */
    @Override
    public boolean isCollect(String userId, Long postId) {
        String key = RedisConstant.USER_COLLECTED_POSTS + userId;
        Boolean isMember = redisUtil.setIsMember(key, postId);

        // 缓存未命中，从数据库查询
        if (isMember == null) {
            boolean dbResult = postMapper.isCollect(userId, postId) > 0;
            if (dbResult) {
                redisUtil.setAdd(key, postId);
            }
            return dbResult;
        }
        return isMember;
    }

    /**
     * 获取帖子收藏数（从 Redis 读取）
     */
    public Long getPostCollectCount(Long postId) {
        String key = RedisConstant.POST_COLLECT_COUNT + postId;
        Long count = redisUtil.getLong(key);

        if (count == null) {
            int dbCount = postMapper.getCollectsCountByPostId(postId);
            redisUtil.set(key, dbCount, RedisConstant.COUNT_EXPIRE, TimeUnit.SECONDS);
            return (long) dbCount;
        }
        return count;
    }

    @Override
    public List<Post> selectRandomPostByCollect(String userId) {
        return postMapper.selectRandomPostByCollect(userId);
    }

    // ==================== Redis 优化：浏览量 ====================

    /**
     * 增加浏览量（仅 Redis，异步同步到数据库）
     */
    @Override
    public void increaseViewCount(Long postId) {
        String key = RedisConstant.POST_VIEW_COUNT + postId;
        redisUtil.increment(key);
        updatePostHotScore(postId, RedisConstant.VIEW_SCORE);
    }

    /**
     * 获取帖子浏览量
     */
    public Long getPostViewCount(Long postId) {
        String key = RedisConstant.POST_VIEW_COUNT + postId;
        Long count = redisUtil.getLong(key);

        if (count == null) {
            Post post = postMapper.selectById(postId);
            int dbCount = post != null ? post.getView() : 0;
            redisUtil.set(key, dbCount, RedisConstant.COUNT_EXPIRE, TimeUnit.SECONDS);
            return (long) dbCount;
        }
        return count;
    }

    // ==================== Redis 优化：转发量 ====================

    @Override
    public void increaseShareCount(Long postId) {
        Post post = postMapper.selectByPk(postId);
        post.setForward(post.getForward() + 1);
        postMapper.update(post);
        log.info("帖子{}转发量: {}", postId, post.getForward());
    }

    // ==================== Redis 优化：热帖排行榜 ====================

    /**
     * 更新帖子热度分数
     */
    private void updatePostHotScore(Long postId, double deltaScore) {
        String key = RedisConstant.HOT_POSTS_DAILY;
        redisUtil.zsetIncrementScore(key, postId, deltaScore);

        // 如果是新key，设置过期时间（24小时）
        Long expire = redisUtil.getExpire(key);
        if (expire == null || expire == -1) {
            redisUtil.expire(key, RedisConstant.HOT_POSTS_EXPIRE, TimeUnit.SECONDS);
        }
    }

    /**
     * 获取热帖排行榜
     */
    public List<Post> getHotPostsFromRedis(int top) {
        Set<Object> postIds = redisUtil.zsetReverseRange(RedisConstant.HOT_POSTS_DAILY, 0, top - 1);

        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ids = postIds.stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());

        return postMapper.selectBatchIds(ids);
    }

    // ==================== 其他方法 ====================

    @Override
    public IPage<Post> selectPageByCollectUserId(Page<Post> page, String userId) {
        IPage<Post> result = postMapper.selectPageByCollectUserId(page, userId);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public IPage<Post> selectPageByLikeUserId(Page<Post> page, String userId) {
        IPage<Post> result = postMapper.selectPageByLikeUserId(page, userId);
        spliceAuthorAvatarPage(result);
        return result;
    }

    @Override
    public void top(Long postId) {
        Post post = postMapper.selectByPk(postId);
        post.setTop(!post.isTop());
        postMapper.update(post);
    }

    @Override
    public void essence(Long postId) {
        Post post = postMapper.selectByPk(postId);
        post.setEssence(!post.isEssence());
        postMapper.update(post);
    }

    // ==================== 缓存初始化（应用启动时调用） ====================

    /**
     * 从数据库初始化 Redis 缓存
     */
    public void initCacheFromDB() {
        log.info("开始从数据库初始化Redis缓存...");
        // TODO: 实现缓存预热逻辑
        // 1. 查询所有帖子的点赞数、收藏数、浏览量
        // 2. 批量写入 Redis
        log.info("Redis缓存初始化完成");
    }

    /**
     * 同步 Redis 浏览量到数据库（定时任务调用）
     */
    public void syncViewCountToDB() {
        log.info("开始同步浏览量到数据库...");
        // TODO: 实现同步逻辑
        // 遍历所有 post:view:count:* 的key，批量更新数据库
        log.info("浏览量同步完成");
    }

    // ==================== 定时发布功能 ====================

    /**
     * 发布定时帖子（将草稿状态改为已发布）
     * 由延迟消息消费者调用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishScheduledPost(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            log.warn("定时发布失败：帖子不存在, postId={}", postId);
            return;
        }

        if (post.getIsDeleted() == 1) {
            log.info("定时发布跳过：帖子已删除, postId={}", postId);
            return;
        }

        // 检查是否已发布（防止重复处理）
        if (post.getStatus() != null && post.getStatus() == 1) {
            log.info("定时发布跳过：帖子已发布, postId={}", postId);
            return;
        }

        // 更新帖子状态为已发布，更新时间为当前时间（让帖子出现在最新列表）
        post.setStatus(1); // 已发布
        post.setUpdateTime(new java.util.Date());
        postMapper.updateById(post);

        // 更新热度排行榜
        updatePostHotScore(postId, RedisConstant.VIEW_SCORE);

        log.info("定时发布成功: postId={}, title={}", postId, post.getTitle());
    }

    /**
     * 保存定时发布的帖子（待发布状态）
     * 帖子会立即保存到数据库（status=0），延迟消息到达后改为已发布（status=1）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveScheduledPost(PostVo postVo, String userId, java.util.Date publishTime) {
        // 保存帖子，设置状态为待发布（status=0）
        Post post = Post.builder()
                .title(postVo.getTitle())
                .content(EmojiParser.parseToAliases(postVo.getContent()))
                .userId(Long.valueOf(userId))
                .anonymous(postVo.isAnonymous())
                .status(0) // 待发布状态
                .build();
        postMapper.insert(post);

        // 处理标签
        List<Long> tagIds = new ArrayList<>();
        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
            tagIds = tags.stream().map(Tag::getId).collect(Collectors.toList());
        }

        // 发送延迟消息
        messageProducer.sendDelayedPostPublish(
                post.getId(),
                Long.valueOf(userId),
                post.getTitle(),
                post.getContent(),
                tagIds,
                postVo.isAnonymous(),
                publishTime);

        log.info("定时发布帖子已保存: postId={}, publishTime={}", post.getId(), publishTime);
        return post.getId();
    }
}
