package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import com.liyh.system.mapper.TipMapper;
import com.liyh.system.service.TipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author LiYH
 * @Description 每日一句接口实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class TipServiceImpl extends ServiceImpl<TipMapper, Tip> implements TipService {
    @Autowired
    private TipMapper tipMapper;

    @Override
    public Tip getRandomTip() {
        return tipMapper.getRandomTip();
    }

    @Override
    public IPage<Tip> selectPage(Page<Tip> tip) {
        return tipMapper.selectPage(tip);
    }
}
