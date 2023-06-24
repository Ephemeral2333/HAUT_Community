package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import com.liyh.model.vo.TipPostVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/20 9:51
 **/
@Service
public interface TipPostService extends IService<TipPost> {

    void tipPost(TipPostVo tipPostVo, String userId);

    IPage<TipPost> selectPage(Page<TipPost> tipPostPage);

    void pass(Long id);

    void refuse(Long id);

    IPage<TipPost> selectPageByUserId(Page<TipPost> tipPostPage, String userId);
}
