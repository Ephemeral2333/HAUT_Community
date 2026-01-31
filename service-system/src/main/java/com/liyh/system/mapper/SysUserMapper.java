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
    IPage<SysUser> selectPage(@Param("page") Page<SysUser> page, @Param("vo") SysUserQueryVo userQueryVo);

    SysUser selectByUserName(@Param("username") String username);

    void updateByEntity(@Param("user") SysUser user);

    SysUser selectByEmail(@Param("email") String email);

    UserVo getFrontInfo(@Param("id") Long id);

    String getNameById(@Param("id") Long id);

    String getAvatarById(@Param("id") Long id);

    String selectNickNameById(@Param("id") Long id);

    void updatePhoto(@Param("url") String url, @Param("id") Long id);

    String getEmailById(@Param("id") Long id);

    void updateProfile(@Param("userVo") UserVo userVo);
}