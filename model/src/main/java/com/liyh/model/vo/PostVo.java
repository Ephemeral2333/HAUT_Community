package com.liyh.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author LiYH
 * @Description 帖子传输对象
 * @Date 2023/6/7 21:34
 **/
@Data
public class PostVo {
    private Long id;
    private String content;
    private String title;
    private List<String> tags;
    private boolean anonymous;   // 是否匿名
}
