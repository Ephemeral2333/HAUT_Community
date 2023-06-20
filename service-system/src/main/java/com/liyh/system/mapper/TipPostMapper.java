package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author LiYH
 * @Description TipPostmapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface TipPostMapper extends BaseMapper<TipPost> {
    IPage<TipPost> selectPage(Page<TipPost> tipPostPage);
}
