package com.liyh.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liyh.model.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author LiYH
 * @Description 公告板
 * @Date 2023/6/5 17:28
 **/
@Data
@TableName("billboard")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Billboard extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("content")
    private String content;
}
