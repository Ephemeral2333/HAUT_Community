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
 * @Description 标签类
 * @Date 2023/6/6 21:18
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tag")
public class Tag implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField(exist = false)
    private int topicCount;

    @TableField(exist = false)
    private List<Post> posts;
}
