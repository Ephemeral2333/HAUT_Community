package com.liyh.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author LiYH
 * @Description 用户信息统计（包括文章总数，获赞数和获收藏数）
 * @Date 2023/6/21 16:13
 **/
@Data
@Builder
@AllArgsConstructor
public class UserInfoCountVo {
    private Long id;

    private String name;
    /**
     * 文章总数
     */
    private Integer articleCount;
    /**
     * 获赞数
     */
    private Integer likeCount;
    /**
     * 获收藏数
     */
    private Integer collectCount;
    /**
     * 总阅览量
     */
    private Integer viewCount;

    private String avatar;
}
