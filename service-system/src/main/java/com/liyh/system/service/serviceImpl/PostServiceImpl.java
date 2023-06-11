package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.PostVo;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.PostService;
import com.liyh.system.service.TagService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子service实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private TagService tagService;

    @Override
    public IPage<Post> selectPage(Page<Post> tip) {
        return postMapper.selectPage(tip);
    }

    @Override
    public IPage<Post> selectPageByHot(Page<Post> tip) {
        return postMapper.selectPageByHot(tip);
    }

    @Override
    public IPage<Post> selectPageByTime(Page<Post> tip) {
        return postMapper.selectPageByTime(tip);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(PostVo postVo, String userId) {
        Post post = Post.builder()
                .title(postVo.getTitle())
                .content(EmojiParser.parseToAliases(postVo.getContent()))
                .userId(Long.valueOf(userId))
                .build();
        postMapper.insert(post);

        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
        }
    }

    /**
     * @Author LiYH
     * @Description 获取帖子详情
     * @Date 22:27 2023/6/7
     * @Param [id]
     * @return com.liyh.model.entity.Post
     **/
    @Override
    public Post selectByPk(Long id) {
        return postMapper.selectByPk(id);
    }

    @Override
    public IPage<Post> selectPageByUserId(Page<Post> page, String userId) {
        return postMapper.selectPageByUserId(page, userId);
    }

    @Override
    public List<Post> selectPostRandom() {
        return postMapper.selectPostRandom();
    }
}
