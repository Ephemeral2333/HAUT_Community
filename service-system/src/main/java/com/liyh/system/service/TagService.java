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

    List<Tag> getHotTags();

    String getNameById(Long id);

    void deleteTopicTagByTopicId(Long id);

    List<Tag> selectTagsByPostId(Long id);

    // 分页查询标签列表
    IPage<Tag> selectTagsList(Page<Tag> tagPage);

    void saveTag(String name);

    void updateTag(Long id, String name);

    // 在删除标签时，需要删除标签与文章的关联关系
    void removePostTagByTagId(Long id);
}
