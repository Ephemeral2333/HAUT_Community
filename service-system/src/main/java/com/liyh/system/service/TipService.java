package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Tip;

/**
 * @Author LiYH
 * @Description 每日一句接口
 * @Date 2023/6/5 17:46
 **/

public interface TipService extends IService<Tip> {

    Tip getRandomTip();

    IPage<Tip> selectPage(Page<Tip> tip);
}
