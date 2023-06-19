package com.liyh.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Comment;
import com.liyh.model.vo.CommentPostVo;

import java.util.List;

/**
 * @Author LiYH
 * @Description 评论服务接口
 * @Date 2023/6/5 17:46
 **/

public interface CommentService extends IService<Comment> {

    List<Comment> getTopicCommentListByTopicId(Long id, Long userId);

    void pushComments(CommentPostVo commentPostVo, Long userId);

    void favor(Long commentId, Long userId);

    void replyComment(CommentPostVo commentPostVo, Long userId);

    void deleteCommentByPostId(Long id);
}
