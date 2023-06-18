package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyh.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author LiYH
 * @Description 评论mapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getTopicCommentListByTopicId(Long id);

    List<Comment> findChildrenComments(Long id);

    int isFavor(Long id, Long userId);

    void favor(Long commentId, Long userId);

    void unFavor(Long commentId, Long userId);
}
