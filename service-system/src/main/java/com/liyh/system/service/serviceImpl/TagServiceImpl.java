package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Tag;
import com.liyh.system.mapper.TagMapper;
import com.liyh.system.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author LiYH
 * @Description 标签接口实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private TagMapper tagMapper;


    @Override
    public List<Tag> insertTags(List<String> tags) {
        List<Tag> tagList = new ArrayList<>();
        for (String tagName : tags) {
            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", tagName);
            Tag tag = tagMapper.selectOne(queryWrapper);
            if (tag == null) {
                // 由于这里是新增标签，所以默认标签的话题数为1
                tag = Tag.builder().name(tagName).build();
                tagMapper.insert(tag);
            } else {
                tagMapper.updateById(tag);
            }
            tagList.add(tag);
        }
        return tagList;
    }

    @Override
    public void createTopicTag(Long topicId, List<Tag> tags) {
        // 先获取原有的标签
        List<Tag> oldTags = tagMapper.selectTagsByPostId(topicId);

        // 先删除原有的标签，方便更新
        tagMapper.deletePostTagByTopicId(topicId);

        for (Tag tag : tags) {
            tagMapper.createTopicTag(topicId, tag.getId());
        }
    }

    // TODO 这里的热门标签应该是根据话题的数量来排序的，暂时先这样
    @Override
    public List<Tag> getHotTags() {
        return tagMapper.getHotTags();
    }

    @Override
    public String getNameById(Long id) {
        return tagMapper.selectById(id).getName();
    }

    @Override
    public void deleteTopicTagByTopicId(Long id) {
        tagMapper.deletePostTagByTopicId(id);
    }

    @Override
    public List<Tag> selectTagsByPostId(Long id) {
        return tagMapper.selectTagsByPostId(id);
    }

    @Override
    public IPage<Tag> selectTagsList(Page<Tag> tagPage) {
        return tagMapper.selectTagList(tagPage);
    }

    @Override
    public void saveTag(String name) {
        Tag tag = tagMapper.selectOne(new QueryWrapper<Tag>().eq("name", name));
        if (tag == null) {
            tag = Tag.builder().name(name).build();
            tagMapper.insert(tag);
        }
    }

    @Override
    public void updateTag(Long id, String name) {
        Tag tag = tagMapper.selectById(id);
        tag.setName(name);
        tagMapper.updateById(tag);
    }

    @Override
    public void removePostTagByTagId(Long id) {
        tagMapper.deletePostTagByTagId(id);
    }
}
