package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Collect;
import com.liyh.model.entity.Post;
import com.liyh.model.entity.Tag;
import com.liyh.model.vo.PostVo;
import com.liyh.system.mapper.CollectMapper;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.service.CommentService;
import com.liyh.system.service.PostService;
import com.liyh.system.service.TagService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CollectMapper collectMapper;

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
     * @return com.liyh.model.entity.Post
     * @Author LiYH
     * @Description 获取帖子详情
     * @Date 22:27 2023/6/7
     * @Param [id]
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
        // 删除帖子
        postMapper.deleteById(id);
        // 然后删除帖子和标签的关联关系
        tagService.deleteTopicTagByTopicId(id);
        // 删除帖子和评论的关联关系
        commentService.deleteCommentByPostId(id);
    }

    @Override
    public List<Post> selectRandomPostByLike(String userId) {
        return postMapper.selectRandomPostByLike(userId);
    }

    @Override
    public List<Post> selectRandomPostByMy(String userId) {
        return postMapper.selectRandomPostByMy(userId);
    }

    @Override
    public void favor(String userId, Long id) {
        postMapper.favor(userId, id);
    }

    @Override
    public void unfavor(String userId, Long id) {
        postMapper.unfavor(userId, id);
    }

    @Override
    public boolean isFavor(String userId, Long id) {
        return postMapper.isFavor(userId, id) > 0;
    }

    @Override
    public void increaseShareCount(Long id) {
        Post post = postMapper.selectByPk(id);
        post.setForward(post.getForward() + 1);
        log.info("转发量" + String.valueOf(post.getForward()));
        postMapper.update(post);
    }

    @Override
    public IPage<Post> selectPageByCollectUserId(Page<Post> page, String userId) {
        return postMapper.selectPageByCollectUserId(page, userId);
    }

    @Override
    public IPage<Post> selectPageByLikeUserId(Page<Post> page, String userId) {
        return postMapper.selectPageByLikeUserId(page, userId);
    }

    @Override
    public boolean isCollect(String userId, Long id) {
        return postMapper.isCollect(userId, id) > 0;
    }

    @Override
    public void collect(String userId, Long id) {
        if (isCollect(userId, id)) {
            collectMapper.unCollect(userId, id);
        } else {
            Collect collect = Collect.builder()
                    .userId(Long.valueOf(userId))
                    .topicId(id)
                    .build();
            collectMapper.insert(collect);
        }
    }

    @Override
    public List<Post> selectRandomPostByCollect(String userId) {
        return postMapper.selectRandomPostByCollect(userId);
    }

    @Override
    public void top(Long id) {
        Post post = postMapper.selectByPk(id);
        post.setTop(!post.isTop());
        postMapper.update(post);
    }

    @Override
    public void essence(Long id) {
        Post post = postMapper.selectByPk(id);
        post.setEssence(!post.isEssence());
        postMapper.update(post);
    }
}
