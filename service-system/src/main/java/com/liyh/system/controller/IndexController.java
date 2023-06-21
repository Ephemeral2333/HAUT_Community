package com.liyh.system.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.liyh.common.result.Result;
import com.liyh.common.result.ResultCodeEnum;
import com.liyh.common.utils.JwtHelper;
import com.liyh.common.utils.MD5;
import com.liyh.common.utils.VCodeUtil;
import com.liyh.model.system.SysUser;
import com.liyh.model.vo.LoginVo;
import com.liyh.model.vo.RegisterVo;
import com.liyh.model.vo.UserVo;
import com.liyh.system.exception.AuthException;
import com.liyh.system.service.EmailService;
import com.liyh.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
        log.info("sysUser:{}", sysUser);
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

    @ApiOperation(value = "注册")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterVo registerVo) throws Exception {
        if (sysUserService.getByUsername(registerVo.getUsername()) != null) {
            return Result.alreadyUserName();
        }
        String code = redisTemplate.opsForValue().get(registerVo.getEmail() + "verify");
        if (!code.equals(registerVo.getCode())) {
            return Result.verifyError();
        }
        sysUserService.register(registerVo);
        return Result.ok();
    }

    @ApiOperation(value = "发送邮箱验证码")
    @GetMapping("sendCode")
    public Result sendCode(@RequestParam String email) {
        if (sysUserService.getByEmail(email) != null) {
            return Result.alreadyEmail();
        }
        log.info(email);
        String verifyCode = VCodeUtil.verifyCode(6);
        redisTemplate.opsForValue().set(email + "verify", verifyCode, 5 * 60, TimeUnit.SECONDS);
        log.info(verifyCode);
        emailService.sendEmail(email, verifyCode);
        return Result.ok();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result<UserVo> info(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        UserVo sysUser = sysUserService.getUserInfo(Long.parseLong(userId));
        return Result.ok(sysUser);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        redisTemplate.delete(userId);
        return Result.ok();
    }

    @ApiOperation("保存头像")
    @PostMapping("/savePhoto/{id}")
    public Result saveAvatar(@RequestBody JsonNode jsonNode, @PathVariable("id") Long id) {
        String url = jsonNode.get("url").asText();
        url = URLDecoder.decode(url);
        sysUserService.saveAvatar(url, id);
        return Result.ok();
    }

    @ApiOperation("修改密码")
    @PostMapping("/modify/password")
    public Result modifyPass(@RequestParam("pass") String pass,
                             HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        sysUserService.modifyPass(pass, userId);
        return Result.ok();
    }

    @ApiOperation("修改个人信息")
    @PostMapping("/update/profile")
    public Result updateProfile(@RequestBody UserVo userVo) {
        sysUserService.updateProfile(userVo);
        return Result.ok();
    }
}
