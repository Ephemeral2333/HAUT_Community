package com.liyh.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/5/25 19:39
 **/
@Data
public class Pagination implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer currentPage;

    private Integer pageSize;

    private boolean background;

    private Integer total;
}
