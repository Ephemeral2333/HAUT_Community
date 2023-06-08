package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tip;
import com.liyh.model.vo.PostVo;

/**
 * @Author LiYH
 * @Description 帖子接口
 * @Date 2023/6/5 17:46
 **/

public interface PostService extends IService<Post> {

    IPage<Post> selectPage(Page<Post> tip);

    IPage<Post> selectPageByHot(Page<Post> tip);

    IPage<Post> selectPageByTime(Page<Post> tip);

    void savePost(PostVo postVo, String userId);

    // 获取帖子详情
    Post selectByPk(Long id);
}