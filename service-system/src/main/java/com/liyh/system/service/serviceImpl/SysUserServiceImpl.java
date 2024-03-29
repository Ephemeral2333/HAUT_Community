package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.common.utils.MD5;
import com.liyh.model.system.SysRole;
import com.liyh.model.system.SysUser;
import com.liyh.model.system.SysUserRole;
import com.liyh.model.vo.*;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.mapper.SysUserMapper;
import com.liyh.system.mapper.SysUserRoleMapper;
import com.liyh.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public IPage<SysUser> selectPage(Page<SysUser> pageParam, SysUserQueryVo userQueryVo) {
        return sysUserMapper.selectPage(pageParam, userQueryVo);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        SysUser sysUser = sysUserMapper.selectById(id);
        sysUser.setStatus(status);
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectByUserName(username);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        sysUser.setPassword(MD5.encrypt("111111"));
        sysUserMapper.updateById(sysUser);
    }

    /**
     * @return void
     * @Author LiYH
     * @Description 分配角色
     * @Date 16:06 2023/6/5
     * @Param [userId, roleIds]
     **/
    @Override
    public void doAssign(String userid, List<Long> roleIds) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userid);
        sysUserRoleMapper.delete(queryWrapper);
        for (Long roleId : roleIds) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(String.valueOf(userid));
            sysUserRole.setRoleId(String.valueOf(roleId));
            sysUserRoleMapper.insert(sysUserRole);
        }
    }

    @Override
    public void deleteRoleUserByUserId(Long id) {
        QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        sysUserRoleMapper.delete(queryWrapper);
    }

    @Override
    public void updateByUserId(SysUser user) {
        sysUserMapper.updateByEntity(user);
    }

    @Override
    public SysUser getByEmail(String email) {
        return sysUserMapper.selectByEmail(email);
    }

    @Override
    public void register(RegisterVo registerVo) {
        registerVo.setPass(MD5.encrypt(registerVo.getPass()));
        SysUser sysUser = new SysUser();
        sysUser.setUsername(registerVo.getUsername());
        sysUser.setPassword(registerVo.getPass());
        sysUser.setEmail(registerVo.getEmail());
        sysUser.setNickname(registerVo.getUsername());
        sysUser.setHeadUrl("http://rw61twimb.hb-bkt.clouddn.com/694ed4f96a14ca2299711140fdafc39b.jpg");
        sysUserMapper.insert(sysUser);
    }

    @Override
    public UserVo getUserInfo(Long id) {
        return sysUserMapper.getFrontInfo(id);
    }

    @Override
    public void saveAvatar(String url, Long id) {
        sysUserMapper.updatePhoto(url, id);
    }

    @Override
    public String getEmailById(Long postManId) {
        return sysUserMapper.getEmailById(postManId);
    }

    @Override
    public void modifyPass(String pass, String userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        sysUser.setPassword(MD5.encrypt(pass));
        sysUserMapper.updateById(sysUser);
    }

    @Override
    public void updateProfile(UserVo userVo) {
        sysUserMapper.updateProfile(userVo);
    }

    @Override
    public UserInfoCountVo getUserInfoCount(String userId) {
        // 获取用户信息
        String username = sysUserMapper.getNameById(Long.valueOf(userId));

        String avatar = sysUserMapper.getAvatarById(Long.valueOf(userId));

        // 获取用户文章总数
        int articleCount = postMapper.getArticleCountByUserId(userId);

        // 获取文章受赞总数
        int likeCount = postMapper.getLikeCountByUserId(userId);

        // 获取文章总收藏数
        int collectCount = postMapper.getCollectCountByUserId(userId);

        // 获取文章总阅览数
        Integer viewCount = postMapper.getViewCountByUserId(userId);

        if (viewCount == null) {
            viewCount = 0;
        }

        return new UserInfoCountVo(Long.valueOf(userId), username,
                articleCount, likeCount, collectCount, viewCount, avatar);
    }
}