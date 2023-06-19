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
                // 由于这里是新增标签，所以默认标签的话题数为1
                tag = Tag.builder().name(tagName).topicCount(1).build();
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
        // 先获取原有的标签
        List<Tag> oldTags = tagMapper.selectTagsByPostId(topicId);
        // 遍历原有的标签，让他们都减1
        for (Tag oldTag : oldTags) {
            oldTag.setTopicCount(oldTag.getTopicCount() - 1);
            tagMapper.updateById(oldTag);
        }

        // 先删除原有的标签，方便更新
        tagMapper.deletePostTagByTopicId(topicId);

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

    @Override
    public void deleteTopicTagByTopicId(Long id) {
        tagMapper.deletePostTagByTopicId(id);
    }

    @Override
    public List<Tag> selectTagsByPostId(Long id) {
        return tagMapper.selectTagsByPostId(id);
    }
}
