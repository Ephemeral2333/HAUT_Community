package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.model.system.SysRole;
import com.liyh.model.vo.SysRoleQueryVo;
import com.liyh.system.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
@Slf4j
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @ApiOperation(value = "查询所有角色")
    @GetMapping("/findAll")
    public Result findAll() {
        List<SysRole> roleList = sysRoleService.list();
        return Result.ok(roleList);
    }

    @ApiOperation(value = "获取分页列表")
    @PostMapping("/getPageList")
    public Result index(@RequestBody SysRoleQueryVo sysRoleQueryVo) {
        Page<SysRole> pageParam = new Page<>(sysRoleQueryVo.getPagination().getCurrentPage(), sysRoleQueryVo.getPagination().getPageSize());
        IPage<SysRole> pageModel = sysRoleService.selectPage(pageParam, sysRoleQueryVo);
        Map<String, Object> map = new HashMap<>();
        map.put("list", pageModel.getRecords());
        map.put("total", pageModel.getTotal());
        map.put("pageSize", pageModel.getSize());
        map.put("currentPage", pageModel.getCurrent());
        return Result.ok(map);
    }

    @ApiOperation(value = "使用角色名称获取角色")
    @GetMapping("/get/{roleName}")
    public Result get(@PathVariable String roleName) {
        SysRole role = sysRoleService.getByName(roleName);
        return Result.ok(role);
    }

    @ApiOperation(value = "新增角色")
    @PostMapping("/save")
    public Result save(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.ok();
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("/update/{id}")
    public Result updateById(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleService.updateById(role);
        return Result.ok();
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("/batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        sysRoleService.removeByIds(idList);
        return Result.ok();
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.getRolesByUserId(userId);
        return Result.ok(roleMap);
    }
}