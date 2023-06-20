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

/**
 * @Author LiYH
 * @Description 每日一句
 * @Date 2023/6/5 21:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("tip")
public class Tip extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("content")
    private String content;

    @TableField("user")
    private String user;

    @TableField("author")
    private String author;
}
