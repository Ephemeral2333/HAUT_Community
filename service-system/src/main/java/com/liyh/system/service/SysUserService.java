package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.SysUserQueryVo;

import java.util.List;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {
    IPage<SysUser> selectPage(Page<SysUser> pageParam, SysUserQueryVo adminQueryVo);
    void updateStatus(String id, Integer status);

    SysUser getByUsername(String username);

    void resetPassword(Long id);

    void doAssign(String userid, List<Long> roleIds);

    void deleteRoleUserByUserId(Long id);

    void updateByUserId(SysUser user);
}