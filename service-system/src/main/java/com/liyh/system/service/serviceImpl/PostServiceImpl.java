package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.PostVo;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.CommentService;
import com.liyh.system.service.PostService;
import com.liyh.system.service.TagService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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

    @Autowired
    private CommentService commentService;

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
    public Post savePost(PostVo postVo, String userId) {
        Post post = Post.builder()
                .title(postVo.getTitle())
                .content(EmojiParser.parseToAliases(postVo.getContent()))
                .userId(Long.valueOf(userId))
                .anonymous(postVo.isAnonymous())
                .build();
        postMapper.insert(post);

        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
        }
        return post;
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

    @Override
    public Post updatePost(PostVo postVo, String userId) {
        // 先找到帖子
        Post post = postMapper.selectByPk(postVo.getId());
        post.setTitle(postVo.getTitle());
        post.setContent(EmojiParser.parseToAliases(postVo.getContent()));
        post.setAnonymous(postVo.isAnonymous());

        // 更新帖子信息
        postMapper.update(post);

        // 更新帖子标签
        if (!ObjectUtils.isEmpty(postVo.getTags())) {
            List<Tag> tags = tagService.insertTags(postVo.getTags());
            tagService.createTopicTag(post.getId(), tags);
        }
        return post;
    }

    @Override
    public IPage<Post> selectAllPage(Page<Post> page) {
        return postMapper.selectAllPage(page);
    }

    @Override
    public void increaseViewCount(Long id) {
        postMapper.increaseViewCount(id);
    }

    @Override
    public IPage<Post> selectPageByTagId(Page<Post> postPage, Long id) {
        return postMapper.selectPageByTagId(postPage, id);
    }

    @Override
    public IPage<Post> searchByKeyword(Page<Post> page, String keyWord) {
        return postMapper.searchByKeyword(page, keyWord);
    }

    @Override
    public void deletePost(Long id) {
        // 先获取帖子的所有标签
        List<Tag> tags = tagService.selectTagsByPostId(id);
        // 让每个标签的引用数减一
        tags.forEach(tag -> {
            tag.setTopicCount(tag.getTopicCount() - 1);
            tagService.updateById(tag);
        });
        // 删除帖子
        postMapper.deleteById(id);
        // 然后删除帖子和标签的关联关系
        tagService.deleteTopicTagByTopicId(id);
        // 删除帖子和评论的关联关系
        commentService.deleteCommentByPostId(id);
    }
}
