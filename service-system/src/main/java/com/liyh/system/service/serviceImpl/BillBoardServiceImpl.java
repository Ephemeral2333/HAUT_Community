package com.liyh.system.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liyh.model.entity.Billboard;
import com.liyh.system.mapper.BillBoardMapper;
import com.liyh.system.service.BillBoardService;
import org.springframework.stereotype.Service;

/**
 * @Author LiYH
 * @Description 公告牌service实现类
 * @Date 2023/6/5 17:47
 **/
@Service
public class BillBoardServiceImpl extends ServiceImpl<BillBoardMapper, Billboard> implements BillBoardService {

}
