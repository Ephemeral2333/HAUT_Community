package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.MD5;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.SysRoleQueryVo;
import com.liyh.model.vo.SysUserQueryVo;
import com.liyh.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
@CrossOrigin
@Slf4j
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "获取分页列表")
    @PostMapping("/getPageList")
    public Result index(@RequestBody Pagination pagination) {
        Page<SysUser> pageParam = new Page<>(pagination.getCurrentPage(),
                pagination.getPageSize());
        SysUserQueryVo sysUserQueryVo = new SysUserQueryVo();
        IPage<SysUser> pageModel = sysUserService.selectPage(pageParam, sysUserQueryVo);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageModel.getRecords());
        map.put("total", pageModel.getTotal());
        map.put("pageSize", pageModel.getSize());
        map.put("currentPage", pageModel.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("/save")
    public Result save(@RequestBody SysUser user) {
        user.setDeptId(user.getParentId());
        user.setPassword("123456");
        user.setNickname("未设置昵称");
        user.setIsDeleted(0);
        // 使用MD5进行加密
        user.setPassword(MD5.encrypt(user.getPassword()));
        boolean isSuccess = sysUserService.save(user);
        // 分配角色
        sysUserService.doAssign(String.valueOf(user.getId()), user.getRoleIds());

        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("/update/{id}")
    public Result updateById(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        user.setDeptId(user.getParentId());
        sysUserService.updateById(user);
        sysUserService.doAssign(String.valueOf(user.getId()), user.getRoleIds());
        return Result.ok();
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.deleteRoleUserByUserId(id);
        sysUserService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "更新状态")
    @PutMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id, @PathVariable Integer status) {
        log.info("id = " + id + " status = " + status);
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

    @ApiOperation(value = "重置密码")
    @PutMapping("resetPassword/{id}")
    public Result resetPass(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.ok();
    }
}