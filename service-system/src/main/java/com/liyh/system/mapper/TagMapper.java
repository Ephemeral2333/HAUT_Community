package com.liyh.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author LiYH
 * @Description 标签Mapper
 * @Date 2023/6/6 21:19
 **/
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> selectTagsByPostId(@Param("id") Long id);

    void createTopicTag(@Param("post") Long post, @Param("tag") Long tag);

    void deletePostTagByTopicId(@Param("topicId") Long topicId);

    IPage<Tag> selectTagList(@Param("page") Page<Tag> tagPage);

    void deletePostTagByTagId(@Param("id") Long id);

    List<Tag> getHotTags();

    int selectCountByTagId(@Param("id") Long id);
}
