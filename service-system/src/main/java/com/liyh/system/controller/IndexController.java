package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.common.result.ResultCodeEnum;
import com.liyh.common.utils.JwtHelper;
import com.liyh.common.utils.MD5;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.LoginVo;
import com.liyh.system.exception.AuthException;
import com.liyh.system.service.SysUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author LiYH
 * @Description 首页控制器
 * @Date 2023/5/9 17:35
 **/
@Api(tags = "用户登录接口")
@RestController
@RequestMapping("/admin/system/index")
@Slf4j
public class IndexController {
    @Autowired
    private SysUserService sysUserService;

    /**
     * @return
     * @Author LiYH
     * @Description Login
     * @Date 17:36 2023/5/9
     * @Param
     **/
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo) {
        // 根据用户名查询数据库
        SysUser sysUser = sysUserService.getByUsername(loginVo.getUsername());
        if (null == sysUser) {
            throw new AuthException(ResultCodeEnum.ACCOUNT_ERROR);
        }
        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())) {
            throw new AuthException(ResultCodeEnum.PASSWORD_ERROR);
        }
        if (sysUser.getStatus().intValue() == 0) {
            throw new AuthException(ResultCodeEnum.ACCOUNT_STOP);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(String.valueOf(sysUser.getId()), sysUser.getUsername()));
        return Result.ok(map);
    }

    /**
     * @return
     * @Author LiYH
     * @Description Info
     * @Date 17:38 2023/5/9
     * @Param
     **/
    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        String token = request.getHeader("token");
        String username = JwtHelper.getUsername(token);
        log.info("username: " + username);
        // 获取用户信息
        Map<String, Object> map = sysUserService.getUserInfo(username);
        return Result.ok(map);
    }
}
