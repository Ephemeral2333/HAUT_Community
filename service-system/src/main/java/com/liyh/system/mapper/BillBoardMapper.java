package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Billboard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author LiYH
 * @Description 公告牌mapper
 * @Date 2023/6/5 17:44
 **/
@Mapper
public interface BillBoardMapper extends BaseMapper<Billboard> {
    Billboard selectOrderByTime();

    IPage<Billboard> selectPage(@Param("page") Page<Billboard> billboard);

    void update(@Param("billboard") Billboard billboard);
}
