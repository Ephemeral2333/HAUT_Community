package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
                tag = Tag.builder().name(tagName).build();
                tagMapper.insert(tag);
            } else {
                tag.setTopicCount(tag.getTopicCount() + 1);
                tagMapper.updateById(tag);
            }
            tagList.add(tag);
        }
        return tagList;
    }

    @Override
    public void createTopicTag(Long topicId, List<Tag> tags) {
        // 先删除原有的标签，方便更新
        tagMapper.deleteByTopicId(topicId);

        for (Tag tag : tags) {
            tagMapper.createTopicTag(topicId, tag.getId());
        }
    }

    @Override
    public List<Tag> getHotTags() {
        return tagMapper.selectList(new QueryWrapper<Tag>().orderByDesc("topic_count").last("limit 10"));
    }

    @Override
    public String getNameById(Long id) {
        return tagMapper.selectById(id).getName();
    }
}
