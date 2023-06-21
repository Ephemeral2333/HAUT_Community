package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyh.model.base.BaseEntity;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子
 * @Date 2023/6/5 21:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("post")
public class Post extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("user_id")
    private Long userId;

    @TableField("comments")
    private int comments;

    @TableField("favor")
    private Long favor;

    @TableField("view")
    private int view;

    @TableField("anonymous")
    private boolean anonymous;

    @TableField("top")
    private boolean top;

    @TableField("collects")
    private int collects;

    @TableField("essence")
    private boolean essence;

    @TableField("forward")
    private Long forward;

    @TableField(exist = false)
    private UserVo author;

    private List<Tag> tags;
}
