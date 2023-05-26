package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.RouterVo;
import com.liyh.model.vo.SysUserQueryVo;
import com.liyh.system.mapper.SysUserMapper;
import com.liyh.system.service.SysMenuService;
import com.liyh.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysMenuService sysMenuService;

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
//        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("username", username);
//        return sysUserMapper.selectOne(queryWrapper);
        return sysUserMapper.selectByUserName(username);
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        // 根据用户名查询用户信息
        SysUser sysUser = this.getByUsername(username);
        log.info("sysUser: " + sysUser);
        Map<String, Object> result = new HashMap<>();
        // 查询权限
        List<RouterVo> routerVoList =  sysMenuService.getUserMenuList(sysUser.getId());

        result.put("name", username);
        result.put("avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        result.put("roles", "[admin]");
        result.put("routers", routerVoList);
        return result;
    }
}