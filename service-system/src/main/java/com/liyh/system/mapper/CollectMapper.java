package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Collect;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author LiYH
 * @Description 收藏 mapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface CollectMapper extends BaseMapper<Collect> {

    void unCollect(String userId, Long id);
}
