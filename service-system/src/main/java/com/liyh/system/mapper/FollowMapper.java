package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyh.model.entity.Follow;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author LiYH
 * @Description 关注mapper
 * @Date 2023/6/6 21:19
 **/
@Mapper
public interface FollowMapper extends BaseMapper<Follow> {

    void follow(String userId, Long parentId);

    void unfollow(String userId, Long parentId);

    int isFollow(Long id, String userId);
}