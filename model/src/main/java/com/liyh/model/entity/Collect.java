package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author LiYH
 * @Description 收藏实体类
 * @Date 2023/6/21 8:38
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("collect")
public class Collect {
    @TableField("id")
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("topic_id")
    private Long topicId;

    @TableField("create_time")
    private String createTime;
}