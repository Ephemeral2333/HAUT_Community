package com.liyh.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Follow;

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
}
