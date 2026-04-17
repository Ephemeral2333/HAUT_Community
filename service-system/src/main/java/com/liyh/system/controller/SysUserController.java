package com.liyh.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liyh.common.result.Result;
import com.liyh.common.utils.JwtHelper;
import com.liyh.common.utils.MD5;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.Pagination;
import com.liyh.model.vo.SysUserQueryVo;
import com.liyh.model.vo.UserInfoCountVo;
import com.liyh.system.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
@CrossOrigin
@Slf4j
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Value("${app.default-password:123456}")
    private String defaultPassword;

    @Value("${app.default-avatar}")
    private String defaultAvatar;

    @Operation(summary = "获取分页列表")
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

    @Operation(summary = "获取用户")
    @GetMapping("/get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @Operation(summary = "保存用户")
    @PostMapping("/save")
    public Result save(@RequestBody SysUser user) {
        user.setDeptId(user.getParentId());
        user.setPassword(defaultPassword);
        user.setNickname(user.getUsername());
        user.setIsDeleted(0);
        user.setHeadUrl(defaultAvatar);
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

    @Operation(summary = "更新用户")
    @PutMapping("/update/{id}")
    public Result updateById(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        user.setDeptId(user.getParentId());
        sysUserService.updateById(user);
        sysUserService.doAssign(String.valueOf(user.getId()), user.getRoleIds());
        return Result.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.deleteRoleUserByUserId(id);
        sysUserService.removeById(id);
        return Result.ok();
    }

    @Operation(summary = "更新状态")
    @PutMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id, @PathVariable Integer status) {
        log.info("更新用户状态, id: {}, status: {}", id, status);
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }

    @Operation(summary = "重置密码")
    @PutMapping("resetPassword/{id}")
    public Result resetPass(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.ok();
    }

    @Operation(summary = "获取用户统计信息")
    @GetMapping("/count/info")
    public Result<UserInfoCountVo> getUserInfoCount(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        UserInfoCountVo userInfoCountVo = sysUserService.getUserInfoCount(userId);
        return Result.ok(userInfoCountVo);
    }
}
