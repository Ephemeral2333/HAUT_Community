package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Follow;
import com.liyh.system.mapper.FollowMapper;
import com.liyh.system.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author LiYH
 * @Description 关注业务实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Autowired
    private FollowMapper followMapper;


    @Override
    public void follow(String userId, Long parentId) {
        followMapper.follow(userId, parentId);
    }

    @Override
    public void unfollow(String userId, Long parentId) {
        followMapper.unfollow(userId, parentId);
    }

    @Override
    public boolean isFollow(Long id, String userId) {
        return followMapper.isFollow(id, userId) > 0;
    }
}
