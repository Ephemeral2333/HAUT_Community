package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyh.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author LiYH
 * @Description 评论实体类
 * @Date 2023/6/16 19:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
@Builder
public class Comment extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("content")
    private String content;

    @TableField("user_id")
    private Long userId;    // 评论者

    @TableField("topic_id")
    private Long topicId;   // 评论的话题

    @TableField("parent_id")
    private Long parentId;  // 父评论id

    @TableField("favor")
    private Long favor;  // 点赞数

    @TableField(exist = false)
    private boolean isFavorite;    // 是否点赞

    @TableField(exist = false)
    private String username;      // 评论者信息

    @TableField(exist = false)
    private List<Comment> children;   // 子评论
}
