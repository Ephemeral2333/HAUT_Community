package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Follow;
import com.liyh.model.vo.FollowerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiYH
 * @Description 关注mapper
 * @Date 2023/6/6 21:19
 **/
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    void follow(@Param("userId") String userId, @Param("parentId") Long parentId);

    void unfollow(@Param("userId") String userId, @Param("parentId") Long parentId);

    int isFollow(@Param("id") Long id, @Param("userId") String userId);

    /**
     * @return java.lang.Integer
     * @Author LiYH
     * @Description 统计粉丝数
     * @Date 22:42 2023/6/11
     * @Param [userId]
     **/
    Integer selectFollowerCountByUserId(@Param("userId") String userId);

    IPage<FollowerVo> getFollowList(@Param("page") Page<FollowerVo> pageParam, @Param("id") Long id);

    IPage<FollowerVo> getFansList(@Param("page") Page<FollowerVo> pageParam, @Param("id") Long id);
}
