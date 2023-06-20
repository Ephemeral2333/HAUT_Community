package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.SysUserQueryVo;
import com.liyh.model.vo.UserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    IPage<SysUser> selectPage(Page<SysUser> page, @Param("vo") SysUserQueryVo userQueryVo);

    SysUser selectByUserName(String username);

    void updateByEntity(SysUser user);

    SysUser selectByEmail(String email);

    UserVo getFrontInfo(Long id);

    String getNameById(Long id);

    String getAvatarById(Long id);

    String selectNickNameById(Long id);

    void updatePhoto(String url, Long id);

    String getEmailById(Long id);
}