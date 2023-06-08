package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Billboard;
import com.liyh.system.mapper.BillBoardMapper;
import com.liyh.system.service.BillBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author LiYH
 * @Description 公告牌service实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class BillBoardServiceImpl extends ServiceImpl<BillBoardMapper, Billboard> implements BillBoardService {
    @Autowired
    private BillBoardMapper billBoardMapper;

    @Override
    public Billboard selectOrderByTime() {
        return billBoardMapper.selectOrderByTime();
    }

    @Override
    public IPage<Billboard> selectPageList(Page<Billboard> billboards) {
        return billBoardMapper.selectPage(billboards);
    }

    @Override
    public void insertBillBoard(String content) {
        billBoardMapper.insert(new Billboard().setContent(content));
    }

    @Override
    public void updateBillBoard(Long id, String content) {
        Billboard billboard = billBoardMapper.selectById(id);
        billboard.setContent(content);
        billBoardMapper.update(billboard);
    }

    @Override
    public void deleteBillBoard(Long id) {
        billBoardMapper.deleteById(id);
    }
}
