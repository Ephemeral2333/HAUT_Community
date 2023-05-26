package com.liyh.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liyh.model.system.SysDept;

import java.util.List;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/5/26 16:22
 **/
public interface SysDeptService extends IService<SysDept> {
    List<SysDept> findAll();
}
