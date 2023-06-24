package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.PostVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子Mapper
 * @Date 2023/6/6 20:09
 **/
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    IPage<Post> selectPageByHot(Page<Post> tip);

    IPage<Post> selectPageByTime(Page<Post> tip);

    Post selectByPk(Long id);

    IPage<Post> selectPageByUserId(Page<Post> post, String userId);

    Integer selectCountByUserId(String userId);

    List<Post> selectPostRandom();

    void update(Post post);

    IPage<Post> selectAllPage(Page<Post> page);

    void increaseViewCount(Long id);

    IPage<Post> selectPageByTagId(Page<Post> postPage, Long id);

    IPage<Post> searchByKeyword(Page<Post> page, String keyword);

    List<Post> selectRandomPostByLike(String id);

    List<Post> selectRandomPostByMy(String userId);

    void favor(String userId, Long id);

    void unfavor(String userId, Long id);

    int isFavor(String userId, Long id);

    IPage<Post> selectPageByCollectUserId(Page<Post> page, String userId);

    IPage<Post> selectPageByLikeUserId(Page<Post> page, String userId);

    int isCollect(String userId, Long id);

    int getArticleCountByUserId(String userId);

    int getLikeCountByUserId(String userId);

    int getCollectCountByUserId(String userId);

    Integer getViewCountByUserId(String userId);

    List<Post> selectRandomPostByCollect(String id);

    int getCommentsCountByPostId(Long id);

    int getFavoriteCountByPostId(Long id);

    int getCollectsCountByPostId(Long id);
}
