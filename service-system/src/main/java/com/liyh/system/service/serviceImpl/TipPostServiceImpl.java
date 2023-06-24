package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Tip;
import com.liyh.model.entity.TipPost;
import com.liyh.model.vo.TipPostVo;
import com.liyh.system.mapper.TipMapper;
import com.liyh.system.mapper.TipPostMapper;
import com.liyh.system.service.EmailService;
import com.liyh.system.service.SysUserService;
import com.liyh.system.service.TipPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author LiYH
 * @Description TipPost接口实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class TipPostServiceImpl extends ServiceImpl<TipPostMapper, TipPost> implements TipPostService {
    @Autowired
    private TipPostMapper tipPostMapper;

    @Autowired
    private TipMapper tipMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void tipPost(TipPostVo tipPostVo, String userId) {
        TipPost tipPost = TipPost.builder()
                .content(tipPostVo.getContent())
                .author(tipPostVo.getAuthor())
                .postman(tipPostVo.getPostman())
                .postManId(Long.valueOf(userId))
                .build();
        tipPostMapper.insert(tipPost);
    }

    @Override
    public IPage<TipPost> selectPage(Page<TipPost> tip) {
        return tipPostMapper.selectPage(tip);
    }

    @Override
    public void pass(Long id) {
        TipPost tipPost = tipPostMapper.selectById(id);
        Tip tip = Tip.builder()
                .content(tipPost.getContent())
                .author(tipPost.getAuthor())
                .user(tipPost.getPostman())
                .build();
        tipMapper.insert(tip);

        String email = sysUserService.getEmailById(tipPost.getPostManId());
        // 发送邮件告知用户
        emailService.sendPostResultEmail(email, "pass", tipPost.getContent());

        tipPost.setIsAccepted(1);   // 设置为1表示通过
        tipPostMapper.updateById(tipPost);
    }

    @Override
    public void refuse(Long id) {
        TipPost tipPost = tipPostMapper.selectById(id);
        String email = sysUserService.getEmailById(tipPost.getPostManId());

        emailService.sendPostResultEmail(email, "refuse", tipPost.getContent());

        tipPost.setIsAccepted(2);   // 设置为2表示拒绝
        tipPostMapper.updateById(tipPost);
    }

    @Override
    public IPage<TipPost> selectPageByUserId(Page<TipPost> tipPostPage, String userId) {
        return tipPostMapper.selectPageByUserId(tipPostPage, userId);
    }
}
