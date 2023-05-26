package com.liyh.system.service.serviceImpl;

import com.liyh.model.system.SysUser;
import com.liyh.system.custom.CustomUser;
import com.liyh.system.service.SysMenuService;
import com.liyh.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author LiYH
 * @Description
 * @Date 2023/5/10 15:54
 **/
@Component
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        if (sysUser.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        // 根据用户ID查询用户权限
        List<String> permission = sysMenuService.getUserButtonList(sysUser.getId());
        // 转换为security的权限集合
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String s : permission) {
            authorities.add(new SimpleGrantedAuthority(s.trim()));
        }
        return new CustomUser(sysUser, authorities);
    }
}
