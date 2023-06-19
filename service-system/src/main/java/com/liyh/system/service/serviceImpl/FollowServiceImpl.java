package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Follow;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.FollowerVo;
import com.liyh.system.mapper.FollowMapper;
import com.liyh.system.mapper.SysUserMapper;
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

    @Autowired
    private SysUserMapper sysUserMapper;


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

    @Override
    public IPage<FollowerVo> getFollowList(Page<FollowerVo> pageParam, String username) {
        SysUser sysUser = sysUserMapper.selectByUserName(username);
        return followMapper.getFollowList(pageParam, sysUser.getId());
    }

    @Override
    public IPage<FollowerVo> getFansList(Page<FollowerVo> pageParam, String username) {
        SysUser sysUser = sysUserMapper.selectByUserName(username);
        return followMapper.getFansList(pageParam, sysUser.getId());
    }
}
