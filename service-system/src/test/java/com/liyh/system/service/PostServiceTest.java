package com.liyh.system.service;

import com.liyh.common.constant.RedisConstant;
import com.liyh.model.entity.Post;
import com.liyh.model.system.SysUser;
import com.liyh.system.mapper.CollectMapper;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.mq.producer.MessageProducer;
import com.liyh.system.service.serviceImpl.PostServiceImpl;
import com.liyh.system.utils.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Unit Test")
class PostServiceTest {

    @Mock private PostMapper postMapper;
    @Mock private RedisUtil redisUtil;
    @Mock private MessageProducer messageProducer;
    @Mock private SysUserService sysUserService;
    @Mock private TagService tagService;
    @Mock private CommentService commentService;
    @Mock private CollectMapper collectMapper;
    @Mock private FileService fileService;

    @InjectMocks
    private PostServiceImpl postService;

    private static final String USER_ID = "1001";
    private static final Long   POST_ID = 999L;

    // ==================== favor ====================

    @Test
    @DisplayName("favor: already liked should skip Redis and DB writes")
    void testFavor_AlreadyLiked_ShouldDoNothing() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(true);

        postService.favor(USER_ID, POST_ID);

        verify(redisUtil, never()).setAdd(anyString(), any());
        verify(postMapper, never()).favor(anyString(), anyLong());
    }

    @Test
    @DisplayName("favor: first like should update Redis and DB")
    void testFavor_FirstTime_ShouldUpdateRedisAndDB() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(false);
        Post mockPost = Post.builder().userId(9999L).title("test").build();
        mockPost.setId(POST_ID);
        SysUser mockUser = new SysUser();
        mockUser.setUsername("alice");
        when(postMapper.selectById(POST_ID)).thenReturn(mockPost);
        when(sysUserService.getById(Long.parseLong(USER_ID))).thenReturn(mockUser);

        postService.favor(USER_ID, POST_ID);

        verify(redisUtil).setAdd(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID);
        verify(redisUtil).increment(RedisConstant.POST_LIKE_COUNT + POST_ID);
        verify(postMapper).favor(USER_ID, POST_ID);
    }

    // ==================== unfavor ====================

    @Test
    @DisplayName("unfavor: not liked should skip")
    void testUnfavor_NotLiked_ShouldDoNothing() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(false);

        postService.unfavor(USER_ID, POST_ID);

        verify(redisUtil, never()).setRemove(anyString(), any());
        verify(postMapper, never()).unfavor(anyString(), anyLong());
    }

    @Test
    @DisplayName("unfavor: liked should remove from Redis and DB")
    void testUnfavor_AlreadyLiked_ShouldUpdateRedisAndDB() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(true);

        postService.unfavor(USER_ID, POST_ID);

        verify(redisUtil).setRemove(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID);
        verify(redisUtil).decrement(RedisConstant.POST_LIKE_COUNT + POST_ID);
        verify(postMapper).unfavor(USER_ID, POST_ID);
    }

    // ==================== isFavor ====================

    @Test
    @DisplayName("isFavor: Redis hit should return Redis result without querying DB")
    void testIsFavor_RedisHit() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(true);

        assertTrue(postService.isFavor(USER_ID, POST_ID));
        verify(postMapper, never()).isFavor(anyString(), anyLong());
    }

    @Test
    @DisplayName("isFavor: Redis miss should fallback to DB and warm cache")
    void testIsFavor_RedisMiss_ShouldFallbackToDB() {
        when(redisUtil.setIsMember(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID)).thenReturn(null);
        when(postMapper.isFavor(USER_ID, POST_ID)).thenReturn(1);

        assertTrue(postService.isFavor(USER_ID, POST_ID));
        verify(redisUtil).setAdd(RedisConstant.USER_LIKED_POSTS + USER_ID, POST_ID);
    }

    // ==================== collect ====================

    @Test
    @DisplayName("collect: not collected should add collect")
    void testCollect_NotCollected_ShouldAdd() {
        when(redisUtil.setIsMember(RedisConstant.USER_COLLECTED_POSTS + USER_ID, POST_ID)).thenReturn(false);

        postService.collect(USER_ID, POST_ID);

        verify(redisUtil).setAdd(RedisConstant.USER_COLLECTED_POSTS + USER_ID, POST_ID);
        verify(collectMapper).insert(any());
    }

    @Test
    @DisplayName("collect: already collected should remove (toggle)")
    void testCollect_AlreadyCollected_ShouldRemove() {
        when(redisUtil.setIsMember(RedisConstant.USER_COLLECTED_POSTS + USER_ID, POST_ID)).thenReturn(true);

        postService.collect(USER_ID, POST_ID);

        verify(redisUtil).setRemove(RedisConstant.USER_COLLECTED_POSTS + USER_ID, POST_ID);
        verify(collectMapper).unCollect(USER_ID, POST_ID);
        verify(collectMapper, never()).insert(any());
    }

    // ==================== publishScheduledPost ====================

    @Test
    @DisplayName("publishScheduledPost: post not found should return without update")
    void testPublishScheduledPost_NotFound() {
        when(postMapper.selectById(POST_ID)).thenReturn(null);

        postService.publishScheduledPost(POST_ID);

        verify(postMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("publishScheduledPost: deleted post should skip")
    void testPublishScheduledPost_Deleted() {
        Post p = new Post();
        p.setId(POST_ID);
        p.setIsDeleted(1);
        when(postMapper.selectById(POST_ID)).thenReturn(p);

        postService.publishScheduledPost(POST_ID);

        verify(postMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("publishScheduledPost: already published should skip (idempotent)")
    void testPublishScheduledPost_AlreadyPublished() {
        Post p = Post.builder().status(1).build();
        p.setId(POST_ID);
        p.setIsDeleted(0);
        when(postMapper.selectById(POST_ID)).thenReturn(p);

        postService.publishScheduledPost(POST_ID);

        verify(postMapper, never()).updateById(any());
    }

    @Test
    @DisplayName("publishScheduledPost: pending post should be set to status=1")
    void testPublishScheduledPost_Pending_ShouldPublish() {
        Post p = Post.builder().status(0).title("test").build();
        p.setId(POST_ID);
        p.setIsDeleted(0);
        when(postMapper.selectById(POST_ID)).thenReturn(p);
        when(redisUtil.getExpire(anyString())).thenReturn(-1L);

        postService.publishScheduledPost(POST_ID);

        verify(postMapper).updateById(argThat(post -> post.getStatus() == 1));
    }

    // ==================== deletePost ====================

    @Test
    @DisplayName("deletePost: should clear Redis cache for likes, collects, views")
    void testDeletePost_ShouldClearRedisCache() {
        postService.deletePost(POST_ID);

        verify(redisUtil).delete(RedisConstant.POST_LIKE_COUNT    + POST_ID);
        verify(redisUtil).delete(RedisConstant.POST_COLLECT_COUNT + POST_ID);
        verify(redisUtil).delete(RedisConstant.POST_VIEW_COUNT    + POST_ID);
    }
}
