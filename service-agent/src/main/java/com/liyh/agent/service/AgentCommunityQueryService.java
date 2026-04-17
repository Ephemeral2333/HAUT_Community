package com.liyh.agent.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.model.entity.Post;
import com.liyh.model.vo.UserVo;
import com.liyh.system.mapper.PostMapper;
import com.liyh.system.mapper.SysUserMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentCommunityQueryService {

    private final PostMapper postMapper;
    private final SysUserMapper sysUserMapper;

    public List<AgentPostSummary> searchPosts(String keyword, int size) {
        Page<Post> page = new Page<>(1, size);
        return postMapper.searchByKeyword(page, keyword).getRecords().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public List<AgentPostSummary> getHotPosts(int size) {
        Page<Post> page = new Page<>(1, size);
        return postMapper.selectPageByHot(page).getRecords().stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    public UserVo getUserInfo(Long userId) {
        return sysUserMapper.getFrontInfo(userId);
    }

    private AgentPostSummary toSummary(Post post) {
        return AgentPostSummary.builder()
                .id(post.getId())
                .title(post.getTitle())
                .view(post.getView())
                .favor(post.getFavor())
                .build();
    }

    @Data
    @Builder
    public static class AgentPostSummary {
        private Long id;
        private String title;
        private int view;
        private int favor;
    }
}
