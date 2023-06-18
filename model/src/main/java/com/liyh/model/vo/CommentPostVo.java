package com.liyh.model.vo;

import lombok.Getter;

/**
 * @Author LiYH
 * @Description 评论提交数据传输对象
 * @Date 2023/6/16 20:54
 **/
@Getter
public class CommentPostVo {
    private String content;
    private Long topicId;
}
