package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Tip;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author LiYH
 * @Description 每日一句mapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface TipMapper extends BaseMapper<Tip> {

    Tip getRandomTip();

    IPage<Tip> selectPage(Page<Tip> tip);
}
