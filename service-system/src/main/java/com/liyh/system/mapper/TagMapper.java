package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyh.model.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author LiYH
 * @Description 标签Mapper
 * @Date 2023/6/6 21:19
 **/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> selectTagsByPostId(Long id);

    void createTopicTag(Long post, Long tag);

    void deletePostTagByTopicId(Long topicId);
}
