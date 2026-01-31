package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.PostVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子Mapper
 * @Date 2023/6/6 20:09
 **/
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    IPage<Post> selectPageByHot(@Param("page") Page<Post> tip);

    IPage<Post> selectPageByTime(@Param("page") Page<Post> tip);

    Post selectByPk(@Param("id") Long id);

    IPage<Post> selectPageByUserId(@Param("page") Page<Post> post, @Param("userId") String userId);

    Integer selectCountByUserId(@Param("userId") String userId);

    List<Post> selectPostRandom();

    void update(@Param("post") Post post);

    IPage<Post> selectAllPage(@Param("page") Page<Post> page);

    void increaseViewCount(@Param("id") Long id);

    IPage<Post> selectPageByTagId(@Param("page") Page<Post> postPage, @Param("id") Long id);

    IPage<Post> searchByKeyword(@Param("page") Page<Post> page, @Param("keyword") String keyword);

    List<Post> selectRandomPostByLike(@Param("id") String id);

    List<Post> selectRandomPostByMy(@Param("userId") String userId);

    void favor(@Param("userId") String userId, @Param("id") Long id);

    void unfavor(@Param("userId") String userId, @Param("id") Long id);

    int isFavor(@Param("userId") String userId, @Param("id") Long id);

    IPage<Post> selectPageByCollectUserId(@Param("page") Page<Post> page, @Param("userId") String userId);

    IPage<Post> selectPageByLikeUserId(@Param("page") Page<Post> page, @Param("userId") String userId);

    int isCollect(@Param("userId") String userId, @Param("id") Long id);

    int getArticleCountByUserId(@Param("userId") String userId);

    int getLikeCountByUserId(@Param("userId") String userId);

    int getCollectCountByUserId(@Param("userId") String userId);

    Integer getViewCountByUserId(@Param("userId") String userId);

    List<Post> selectRandomPostByCollect(@Param("id") String id);

    int getCommentsCountByPostId(@Param("id") Long id);

    int getFavoriteCountByPostId(@Param("id") Long id);

    int getCollectsCountByPostId(@Param("id") Long id);
}
