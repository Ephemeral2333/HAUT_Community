# Redis 深度应用实施方案（优化版）

## 架构设计原则

### ✅ 采用分层架构

```
RedisUtil (工具层)
    ↓ 提供通用方法
Service (业务层)
    ↓ 封装业务逻辑
Controller (控制层)
```

**优点**：
- ✅ 单一职责，每个 Service 管理自己的业务
- ✅ 代码清晰，逻辑不混乱
- ✅ 易扩展，新增功能在对应 Service 中实现
- ✅ 易测试，每个 Service 独立测试

---

## 一、创建通用 Redis 工具类

### 1.1 RedisUtil

**文件**：`common/common-util/src/main/java/com/liyh/common/utils/RedisUtil.java`

```java
package com.liyh.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类 - 提供通用的 Redis 操作方法
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== String 操作 ====================

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置值并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 自增
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增指定值
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 自减
     */
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 删除键
     */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 判断键是否存在
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    // ==================== Set 操作 ====================

    /**
     * Set 添加成员
     */
    public Long setAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * Set 移除成员
     */
    public Long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * Set 判断是否是成员
     */
    public Boolean setIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * Set 获取所有成员
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Set 获取成员数量
     */
    public Long setSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ==================== ZSet 操作 ====================

    /**
     * ZSet 添加成员
     */
    public Boolean zsetAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * ZSet 增加分数
     */
    public Double zsetIncrementScore(String key, Object value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * ZSet 获取排名（降序，分数从高到低）
     */
    public Set<Object> zsetReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * ZSet 获取成员分数
     */
    public Double zsetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * ZSet 移除成员
     */
    public Long zsetRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    // ==================== 通用操作 ====================

    /**
     * 获取剩余过期时间（秒）
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
```

---

## 二、在 PostService 中实现 Redis 逻辑

### 2.1 在 PostServiceImpl 中添加方法

**文件**：`service-system/src/main/java/com/liyh/system/service/serviceImpl/PostServiceImpl.java`

在现有 `PostServiceImpl` 类中添加以下内容：

```java
@Autowired
private RedisUtil redisUtil;

// ==================== Redis 相关方法 ====================

/**
 * 点赞帖子（Redis + DB）
 */
@Override
@Transactional(rollbackFor = Exception.class)
public void favor(String userId, Long postId) {
    Long uid = Long.parseLong(userId);
    
    // 1. 检查 Redis 是否已点赞
    String userLikedKey = RedisConstant.USER_LIKED_POSTS + uid;
    if (Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, postId))) {
        log.warn("用户{}已点赞帖子{}", userId, postId);
        return;
    }
    
    // 2. Redis 操作
    redisUtil.setAdd(userLikedKey, postId);  // 添加到用户点赞集合
    redisUtil.increment(RedisConstant.POST_LIKE_COUNT + postId);  // 点赞数+1
    updatePostHotScore(postId, 3.0);  // 更新热度
    
    // 3. 数据库操作（保证持久化）
    postMapper.favor(userId, postId);
    
    log.info("用户{}点赞帖子{}", userId, postId);
}

/**
 * 取消点赞（Redis + DB）
 */
@Override
@Transactional(rollbackFor = Exception.class)
public void unfavor(String userId, Long postId) {
    Long uid = Long.parseLong(userId);
    
    // 1. 检查是否已点赞
    String userLikedKey = RedisConstant.USER_LIKED_POSTS + uid;
    if (!Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, postId))) {
        log.warn("用户{}未点赞帖子{}", userId, postId);
        return;
    }
    
    // 2. Redis 操作
    redisUtil.setRemove(userLikedKey, postId);
    redisUtil.decrement(RedisConstant.POST_LIKE_COUNT + postId);
    updatePostHotScore(postId, -3.0);
    
    // 3. 数据库操作
    postMapper.unfavor(userId, postId);
    
    log.info("用户{}取消点赞帖子{}", userId, postId);
}

/**
 * 判断用户是否点赞了帖子（从 Redis）
 */
@Override
public boolean isFavor(String userId, Long postId) {
    String key = RedisConstant.USER_LIKED_POSTS + userId;
    return Boolean.TRUE.equals(redisUtil.setIsMember(key, postId));
}

/**
 * 获取帖子点赞数（从 Redis）
 */
public Long getPostLikeCount(Long postId) {
    String key = RedisConstant.POST_LIKE_COUNT + postId;
    Object count = redisUtil.get(key);
    if (count == null) {
        // 缓存未命中，从数据库查询并写入缓存
        int dbCount = postMapper.getFavoriteCountByPostId(postId);
        redisUtil.set(key, dbCount, RedisConstant.COUNT_EXPIRE, TimeUnit.SECONDS);
        return (long) dbCount;
    }
    return Long.parseLong(count.toString());
}

/**
 * 收藏帖子（Redis + DB）
 */
@Override
@Transactional(rollbackFor = Exception.class)
public void collect(String userId, Long postId) {
    Long uid = Long.parseLong(userId);
    
    // 检查是否已收藏
    String userCollectedKey = RedisConstant.USER_COLLECTED_POSTS + uid;
    boolean isCollected = Boolean.TRUE.equals(redisUtil.setIsMember(userCollectedKey, postId));
    
    if (isCollected) {
        // 取消收藏
        redisUtil.setRemove(userCollectedKey, postId);
        redisUtil.decrement(RedisConstant.POST_COLLECT_COUNT + postId);
        updatePostHotScore(postId, -5.0);
        collectMapper.unCollect(userId, postId);
        log.info("用户{}取消收藏帖子{}", userId, postId);
    } else {
        // 收藏
        redisUtil.setAdd(userCollectedKey, postId);
        redisUtil.increment(RedisConstant.POST_COLLECT_COUNT + postId);
        updatePostHotScore(postId, 5.0);
        
        Collect collect = Collect.builder()
                .userId(uid)
                .topicId(postId)
                .build();
        collectMapper.insert(collect);
        log.info("用户{}收藏帖子{}", userId, postId);
    }
}

/**
 * 判断是否收藏（从 Redis）
 */
@Override
public boolean isCollect(String userId, Long postId) {
    String key = RedisConstant.USER_COLLECTED_POSTS + userId;
    return Boolean.TRUE.equals(redisUtil.setIsMember(key, postId));
}

/**
 * 增加浏览量（仅 Redis，定时同步）
 */
@Override
public void increaseViewCount(Long postId) {
    String key = RedisConstant.POST_VIEW_COUNT + postId;
    redisUtil.increment(key);
    updatePostHotScore(postId, 1.0);  // 浏览权重：1分
}

/**
 * 更新帖子热度分数
 */
private void updatePostHotScore(Long postId, double deltaScore) {
    String key = RedisConstant.HOT_POSTS_DAILY;
    redisUtil.zsetIncrementScore(key, postId, deltaScore);
    
    // 如果是新key，设置过期时间
    if (redisUtil.getExpire(key) == -1) {
        redisUtil.expire(key, RedisConstant.HOT_POSTS_EXPIRE, TimeUnit.SECONDS);
    }
}

/**
 * 获取热帖排行榜
 */
public List<Post> getHotPosts(int top) {
    Set<Object> postIds = redisUtil.zsetReverseRange(RedisConstant.HOT_POSTS_DAILY, 0, top - 1);
    
    if (postIds == null || postIds.isEmpty()) {
        return Collections.emptyList();
    }
    
    // 批量查询帖子详情
    List<Long> ids = postIds.stream()
            .map(id -> Long.parseLong(id.toString()))
            .collect(Collectors.toList());
    
    return postMapper.selectBatchIds(ids);
}

/**
 * 从数据库初始化 Redis 缓存（应用启动时调用）
 */
public void initCacheFromDB() {
    log.info("开始从数据库初始化Redis缓存...");
    
    // TODO: 1. 初始化点赞数据
    // 查询所有帖子的点赞数，写入 Redis
    
    // TODO: 2. 初始化收藏数据
    // 查询所有帖子的收藏数，写入 Redis
    
    // TODO: 3. 初始化浏览量
    // 查询所有帖子的浏览量，写入 Redis
    
    log.info("Redis缓存初始化完成");
}

/**
 * 定时同步 Redis 数据到数据库
 */
public void syncRedisToDatabase() {
    log.info("开始同步Redis数据到数据库...");
    
    // TODO: 批量更新浏览量到数据库
    
    log.info("数据同步完成");
}
```

---

## 三、修改 PostController 返回数据

### 3.1 修改帖子详情接口

```java
@ApiOperation("获取帖子详情")
@GetMapping("/front/post/{id}")
public Result<Post> getTopic(@PathVariable Long id) {
    // 增加浏览量（仅写Redis）
    postService.increaseViewCount(id);
    
    // 获取帖子详情
    Post post = postService.selectByPk(id);
    
    // 从Redis获取实时计数
    Long likeCount = postService.getPostLikeCount(id);
    post.setFavor(likeCount.intValue());
    
    return Result.ok(post);
}
```

---

## 四、其他 Service 的 Redis 实现

### 4.1 CommentService 中实现评论点赞

```java
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * 评论点赞
     */
    @Override
    public void favor(Long commentId, Long userId) {
        String userLikedKey = "user:liked:comments:" + userId;
        String commentLikeCountKey = "comment:like:count:" + commentId;
        
        boolean isLiked = Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, commentId));
        
        if (isLiked) {
            // 取消点赞
            redisUtil.setRemove(userLikedKey, commentId);
            redisUtil.decrement(commentLikeCountKey);
            commentMapper.unFavor(commentId, userId);
        } else {
            // 点赞
            redisUtil.setAdd(userLikedKey, commentId);
            redisUtil.increment(commentLikeCountKey);
            commentMapper.favor(commentId, userId);
        }
    }
}
```

### 4.2 FollowService 中实现关注缓存

```java
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    /**
     * 关注用户
     */
    @Override
    public void follow(String followerId, Long followedId) {
        // Redis 缓存
        String followingKey = "user:following:" + followerId;  // 我关注的人
        String followerKey = "user:followers:" + followedId;   // 关注我的人
        
        redisUtil.setAdd(followingKey, followedId);
        redisUtil.setAdd(followerKey, Long.parseLong(followerId));
        
        // 数据库
        // ... 原有逻辑
    }
    
    /**
     * 获取关注数
     */
    public Long getFollowingCount(Long userId) {
        String key = "user:following:" + userId;
        return redisUtil.setSize(key);
    }
}
```

---

## 五、架构对比

### ❌ 原方案（集中式）

```java
// 所有业务都在 RedisService
RedisService {
    likePost()      // 帖子点赞
    collectPost()   // 帖子收藏
    likeComment()   // 评论点赞
    followUser()    // 关注用户
    // ... 越来越多
}

// 其他 Service 依赖 RedisService
PostService -> RedisService
CommentService -> RedisService
FollowService -> RedisService
```

**问题**：
- RedisService 越来越臃肿
- 职责不清晰
- 依赖关系复杂

---

### ✅ 优化方案（分布式）

```java
// 通用工具类
RedisUtil {
    set(), get(), increment()
    setAdd(), setIsMember()
    zsetAdd(), zsetIncrementScore()
}

// 每个 Service 管理自己的业务
PostService {
    favor() {
        // 内部调用 redisUtil
        redisUtil.setAdd(...)
        redisUtil.increment(...)
    }
}

CommentService {
    favorComment() {
        // 内部调用 redisUtil
        redisUtil.setAdd(...)
    }
}

FollowService {
    follow() {
        // 内部调用 redisUtil
        redisUtil.setAdd(...)
    }
}
```

**优点**：
- ✅ 职责清晰，每个 Service 管理自己的逻辑
- ✅ RedisUtil 只提供通用方法，不包含业务逻辑
- ✅ 易扩展，新增功能在对应 Service 实现
- ✅ 易测试，每个 Service 独立测试

---

## 六、实施步骤

### Step 1: 创建 RedisUtil
```
common/common-util/src/main/java/com/liyh/common/utils/RedisUtil.java
```

### Step 2: 创建 RedisConstant
```
common/common-util/src/main/java/com/liyh/common/constant/RedisConstant.java
```

### Step 3: 创建 RedisConfig
```
service-system/src/main/java/com/liyh/system/config/RedisConfig.java
```

### Step 4: 修改 PostServiceImpl
在 `PostServiceImpl` 中添加 Redis 相关方法

### Step 5: 修改 CommentServiceImpl
在 `CommentServiceImpl` 中添加 Redis 相关方法

### Step 6: 修改 FollowServiceImpl
在 `FollowServiceImpl` 中添加 Redis 相关方法

### Step 7: 修改 Controller
修改返回数据，从 Redis 获取计数

---

## 七、总结

| 维度 | 集中式（❌） | 分布式（✅） |
|------|------------|-------------|
| 职责划分 | 混乱 | 清晰 |
| 代码组织 | 一个类越来越大 | 分散在各自 Service |
| 易维护性 | 差 | 好 |
| 易扩展性 | 差 | 好 |
| 测试难度 | 高 | 低 |

**最佳实践**：
- RedisUtil 只提供通用的 Redis 操作方法
- 业务逻辑在各自的 Service 中实现
- 每个 Service 负责自己业务相关的 Redis 操作

---

**文档版本**: v2.0（优化版）  
**创建日期**: 2024年
