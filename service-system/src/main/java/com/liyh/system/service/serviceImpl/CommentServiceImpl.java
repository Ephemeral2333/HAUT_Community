package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Comment;
import com.liyh.model.vo.CommentPostVo;
import com.liyh.system.mapper.CommentMapper;
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

    /**
     * @return java.util.List<com.liyh.model.entity.Comment>
     * @Author LiYH
     * @Description 获取某话题评论列表
     * @Date 12:54 2023/6/18
     * @Param [id, userId]
     **/
    @Override
    public List<Comment> getTopicCommentListByTopicId(Long id, Long userId) {
        List<Comment> commentList = commentMapper.getTopicCommentListByTopicId(id);
        if (userId == null) {
            return commentList;
        } else {
            commentList.forEach(comment -> {
                int favor = commentMapper.isFavor(comment.getId(), userId);
                comment.setFavorite(favor != 0);
                traverseAllChildren(comment, userId);
            });
        }
        return commentList;
    }

    @Override
    public void pushComments(CommentPostVo commentPostVo, Long userId) {
        Comment comment = Comment.builder()
                .content(commentPostVo.getContent())
                .topicId(commentPostVo.getTopicId())
                .userId(userId)
                .parentId(0L)
                .build();
        commentMapper.insert(comment);
    }

    /**
     * @return void
     * @Author LiYH
     * @Description 点赞和取消点赞
     * @Date 13:21 2023/6/18
     * @Param [commentId, userId]
     **/
    @Override
    public void favor(Long commentId, Long userId) {
        int favor = commentMapper.isFavor(commentId, userId);
        if (favor == 0) {
            commentMapper.favor(commentId, userId);
        } else {
            commentMapper.unFavor(commentId, userId);
        }
    }

    @Override
    public void replyComment(CommentPostVo commentPostVo, Long userId) {
        Comment comment = Comment.builder()
                .content(commentPostVo.getContent())
                .topicId(commentPostVo.getTopicId())
                .userId(userId)
                .parentId(commentPostVo.getId())
                .build();
        commentMapper.insert(comment);
    }

    @Override
    public void deleteCommentByPostId(Long id) {
        commentMapper.deleteCommentByPostId(id);
    }

    /**
     * @return void
     * @Author LiYH
     * @Description 遍历所有的子评论，判断当前用户是否点赞
     * @Date 14:00 2023/6/18
     * @Param [comment, userId]
     **/
    public void traverseAllChildren(Comment comment, Long userId) {
        // 遍历他的所有子评论
        List<Comment> children = comment.getChildren();
        if (children != null) {
            children.forEach(child -> {
                // 判断是否点赞
                int favor = commentMapper.isFavor(child.getId(), userId);
                child.setFavorite(favor != 0);
                traverseAllChildren(child, userId);
            });
        }
    }
}
