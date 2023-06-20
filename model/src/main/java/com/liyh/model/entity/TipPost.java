package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/20 9:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tip_post")
public class TipPost {
    @TableField("id")
    private Long id;

    @TableField("author")
    private String author;

    @TableField("content")
    private String content;

    @TableField("post_time")
    private String postTime;

    @TableField("postman")
    private String postman;

    @TableField("postman_id")
    private Long postManId;

    @TableField("is_accepted")
    private Integer isAccepted;
}
