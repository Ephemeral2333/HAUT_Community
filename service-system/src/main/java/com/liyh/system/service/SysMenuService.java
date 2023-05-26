package com.liyh.system.service;

import com.liyh.model.system.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.vo.AssginMenuVo;
import com.liyh.model.vo.RouterVo;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树形数据
     *
     * @return
     */
    List<SysMenu> findNodes();

    /**
     * 根据角色获取授权权限数据
     *
     * @return
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);

    /**
     * 保存角色权限
     *
     * @param assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);

    List<RouterVo> getUserMenuList(Long id);

    List<String> getUserButtonList(Long id);
}