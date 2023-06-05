package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.system.SysDept;
import com.liyh.system.mapper.SysDeptMapper;
import com.liyh.system.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/5/26 16:22
 **/
@Transactional
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {
    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Override
    public List<SysDept> findAll() {
        return sysDeptMapper.findAll();
    }
}
