package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.model.system.SysDept;
import com.liyh.model.system.SysUser;
import com.liyh.system.service.SysDeptService;
import com.liyh.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author LiYH
 * @Description 部门管理
 * @Date 2023/5/26 16:21
 **/
@Api(tags = "部门管理接口")
@RestController
@RequestMapping("/admin/system/dept")
@Slf4j
public class SysDeptController {
    @Autowired
    private SysDeptService sysDeptService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * @Author LiYH
     * @Description 获取部门列表
     * @Date 23:26 2023/5/26
     * @Param []
     * @return com.liyh.common.result.Result
     **/
    @ApiOperation(value = "部门列表")
    @GetMapping("/list")
    public Result list() {
        List<SysDept> deptList = sysDeptService.findAll();
        return Result.ok(deptList);
    }

    /**
     * @Author LiYH
     * @Description 新增部门
     * @Date 0:07 2023/5/27
     * @Param [sysDept]
     * @return com.liyh.common.result.Result
     **/
    @ApiOperation(value = "新增部门")
    @PostMapping("/save")
    public Result save(@RequestBody SysDept sysDept){
        SysUser sysUser = sysUserService.getByUsername(sysDept.getPrincipal());
        if (sysUser == null) {
            return Result.noUser(null);
        }
        sysDept.setIsDeleted(0);
        sysDeptService.save(sysDept);
        return Result.ok();
    }

    /**
     * @Author LiYH
     * @Description 更新部门
     * @Date 0:07 2023/5/27
     * @Param [id, sysDept]
     * @return com.liyh.common.result.Result
     **/
    @ApiOperation(value = "更新部门")
    @PutMapping("/update/{id}")
    public Result update(@PathVariable Long id, @RequestBody SysDept sysDept){
        SysUser sysUser = sysUserService.getByUsername(sysDept.getPrincipal());
        if (sysUser == null) {
            return Result.noUser(null);
        }
        sysDept.setIsDeleted(0);
        sysDept.setId(id);
        sysDeptService.updateById(sysDept);
        return Result.ok();
    }

    /**
     * @Author LiYH
     * @Description 删除部门
     * @Date 0:07 2023/5/27
     * @Param [id]
     * @return com.liyh.common.result.Result
     **/
    @ApiOperation("删除部门")
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id){
        sysDeptService.removeById(id);
        return Result.ok();
    }
}
