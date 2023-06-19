package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Follow;
import com.liyh.model.vo.FollowerVo;

import java.util.List;

/**
 * @Author LiYH
 * @Description 关注业务接口
 * @Date 2023/6/5 17:46
 **/

public interface FollowService extends IService<Follow> {

    void follow(String userId, Long parentId);

    void unfollow(String userId, Long parentId);

    boolean isFollow(Long id, String userId);

    // 获取关注列表
    IPage<FollowerVo> getFollowList(Page<FollowerVo> pageParam, String username);

    IPage<FollowerVo> getFansList(Page<FollowerVo> pageParam, String username);
}
