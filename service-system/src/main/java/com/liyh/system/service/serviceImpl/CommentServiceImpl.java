package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.common.constant.RedisConstant;
import com.liyh.system.utils.RedisUtil;
import com.liyh.model.entity.Comment;
import com.liyh.model.entity.Post;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.CommentPostVo;
import com.liyh.system.mapper.CommentMapper;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.mapper.SysUserMapper;
import com.liyh.system.mq.producer.MessageProducer;
import com.liyh.system.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author LiYH
 * @Description 评论接口实现类
 * @Date 2023/6/5 17:47
 **/
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取某话题评论列表
     */
    @Override
    public List<Comment> getTopicCommentListByTopicId(Long topicId, Long userId) {
        List<Comment> commentList = commentMapper.getTopicCommentListByTopicId(topicId);
        
        if (userId == null) {
            return commentList;
        }
        
        // 从 Redis 判断当前用户是否点赞了每条评论
        commentList.forEach(comment -> {
            comment.setFavorite(isCommentLiked(userId, comment.getId()));
            traverseAllChildren(comment, userId);
        });
        
        return commentList;
    }

    /**
     * 发布评论
     */
    @Override
    public void pushComments(CommentPostVo commentPostVo, Long userId) {
        Comment comment = Comment.builder()
                .content(commentPostVo.getContent())
                .topicId(commentPostVo.getTopicId())
                .userId(userId)
                .parentId(0L)
                .build();
        commentMapper.insert(comment);
        
        // 更新帖子评论数（Redis）
        String key = RedisConstant.POST_COMMENT_COUNT + commentPostVo.getTopicId();
        redisUtil.increment(key);
        
        // 发送评论通知
        try {
            Post post = postMapper.selectById(commentPostVo.getTopicId());
            if (post != null && !post.getUserId().equals(String.valueOf(userId))) {
                SysUser fromUser = sysUserMapper.selectById(userId);
                if (fromUser != null) {
                    messageProducer.sendCommentNotify(
                            userId,
                            fromUser.getUsername(),
                            post.getUserId(),
                            post.getId(),
                            post.getTitle(),
                            commentPostVo.getContent()
                    );
                }
            }
        } catch (Exception e) {
            log.warn("发送评论通知失败: {}", e.getMessage());
        }
        
        log.info("用户{}发布评论，帖子ID: {}", userId, commentPostVo.getTopicId());
    }

    /**
     * 评论点赞/取消点赞（Redis + DB）
     */
    @Override
    public void favor(Long commentId, Long userId) {
        String userLikedKey = RedisConstant.USER_LIKED_COMMENTS + userId;
        String commentLikeCountKey = RedisConstant.COMMENT_LIKE_COUNT + commentId;

        boolean isLiked = Boolean.TRUE.equals(redisUtil.setIsMember(userLikedKey, commentId));

        if (isLiked) {
            // 取消点赞
            redisUtil.setRemove(userLikedKey, commentId);
            redisUtil.decrement(commentLikeCountKey);
            commentMapper.unFavor(commentId, userId);
            log.debug("用户{}取消点赞评论{}", userId, commentId);
        } else {
            // 点赞
            redisUtil.setAdd(userLikedKey, commentId);
            redisUtil.increment(commentLikeCountKey);
            commentMapper.favor(commentId, userId);
            log.debug("用户{}点赞评论{}", userId, commentId);
        }
    }

    /**
     * 判断用户是否点赞了评论（从 Redis 读取）
     */
    private boolean isCommentLiked(Long userId, Long commentId) {
        String key = RedisConstant.USER_LIKED_COMMENTS + userId;
        Boolean isMember = redisUtil.setIsMember(key, commentId);
        
        // 缓存未命中，从数据库查询
        if (isMember == null) {
            int dbResult = commentMapper.isFavor(commentId, userId);
            if (dbResult > 0) {
                redisUtil.setAdd(key, commentId);
            }
            return dbResult > 0;
        }
        return isMember;
    }

    /**
     * 获取评论点赞数
     */
    public Long getCommentLikeCount(Long commentId) {
        String key = RedisConstant.COMMENT_LIKE_COUNT + commentId;
        Long count = redisUtil.getLong(key);
        
        if (count == null) {
            // 从数据库查询并写入缓存
            // TODO: 添加查询评论点赞数的SQL
            return 0L;
        }
        return count;
    }

    /**
     * 回复评论
     */
    @Override
    public void replyComment(CommentPostVo commentPostVo, Long userId) {
        Comment comment = Comment.builder()
                .content(commentPostVo.getContent())
                .topicId(commentPostVo.getTopicId())
                .userId(userId)
                .parentId(commentPostVo.getId())
                .build();
        commentMapper.insert(comment);
        
        // 更新帖子评论数
        String key = RedisConstant.POST_COMMENT_COUNT + commentPostVo.getTopicId();
        redisUtil.increment(key);
        
        // 发送回复通知
        try {
            Comment parentComment = commentMapper.selectById(commentPostVo.getId());
            if (parentComment != null && !parentComment.getUserId().equals(userId)) {
                SysUser fromUser = sysUserMapper.selectById(userId);
                if (fromUser != null) {
                    messageProducer.sendReplyNotify(
                            userId,
                            fromUser.getUsername(),
                            parentComment.getUserId(),
                            parentComment.getId(),
                            commentPostVo.getContent()
                    );
                }
            }
        } catch (Exception e) {
            log.warn("发送回复通知失败: {}", e.getMessage());
        }
        
        log.info("用户{}回复评论{}", userId, commentPostVo.getId());
    }

    /**
     * 删除帖子的所有评论
     */
    @Override
    public void deleteCommentByPostId(Long postId) {
        commentMapper.deleteCommentByPostId(postId);
        
        // 清除 Redis 缓存
        redisUtil.delete(RedisConstant.POST_COMMENT_COUNT + postId);
    }

    /**
     * 遍历所有子评论，判断当前用户是否点赞
     */
    private void traverseAllChildren(Comment comment, Long userId) {
        List<Comment> children = comment.getChildren();
        if (children != null && !children.isEmpty()) {
            children.forEach(child -> {
                child.setFavorite(isCommentLiked(userId, child.getId()));
                traverseAllChildren(child, userId);
            });
        }
    }
}
