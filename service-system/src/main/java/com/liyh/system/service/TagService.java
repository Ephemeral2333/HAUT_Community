package com.liyh.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.entity.Tag;
import com.liyh.model.entity.Tip;

import java.util.List;

/**
 * @Author LiYH
 * @Description 标签服务接口
 * @Date 2023/6/5 17:46
 **/

public interface TagService extends IService<Tag> {

    List<Tag> insertTags(List<String> tags);

    void createTopicTag(Long id, List<Tag> tags);
}
