package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Billboard;

/**
 * @Author LiYH
 * @Description 公告牌service
 * @Date 2023/6/5 17:46
 **/
public interface BillBoardService extends IService<Billboard> {
    Billboard selectOrderByTime();

    IPage<Billboard> selectPageList(Page<Billboard> billboards);

    void insertBillBoard(String content);

    void updateBillBoard(Long id, String content);

    void deleteBillBoard(Long id);
}
