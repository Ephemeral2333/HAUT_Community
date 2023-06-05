package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyh.model.system.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author LiYH
 * @Description 部门Mapper
 * @Date 2023/5/26 16:23
 **/
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    List<SysDept> findAll();

    // 根据id查询部门信息，但只提供ID和Name
    SysDept selectByPKForUser(Long id);
}
