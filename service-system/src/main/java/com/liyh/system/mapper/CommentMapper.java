package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyh.model.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiYH
 * @Description 评论mapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getTopicCommentListByTopicId(@Param("id") Long id);

    List<Comment> findChildrenComments(@Param("id") Long id);

    int isFavor(@Param("id") Long id, @Param("userId") Long userId);

    void favor(@Param("commentId") Long commentId, @Param("userId") Long userId);

    void unFavor(@Param("commentId") Long commentId, @Param("userId") Long userId);

    Long getFavorsCount(@Param("id") Long id);

    void deleteCommentByPostId(@Param("id") Long id);
}
