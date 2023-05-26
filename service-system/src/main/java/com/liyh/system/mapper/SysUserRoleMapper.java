package com.liyh.system.mapper;

import com.liyh.model.system.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.liyh.model.system.SysRole;

@Repository
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<SysRole> selectRoleByUserName(String username);
}