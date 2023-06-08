package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author LiYH
 * @Description 关注实体类
 * @Date 2023/6/7 22:38
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("follow")
public class Follow implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;

    @TableField("parent_id")
    private Long parentId;    // 被关注者

    @TableField("follower_id")
    private Long followerId;  // 关注者

    @TableField("is_deleted")
    private Integer isDeleted;
}
