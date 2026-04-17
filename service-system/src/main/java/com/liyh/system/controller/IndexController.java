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
import com.liyh.system.annotation.RateLimit;
import com.liyh.system.mq.producer.MessageProducer;
import com.liyh.system.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@Tag(name = "用户登录接口")
@RestController
@RequestMapping("/admin/system/index")
@Slf4j
public class IndexController {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private com.liyh.system.service.FileService fileService;

    @Autowired
    private MessageProducer messageProducer;

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
    @RateLimit(prefix = "limit:login:", key = "#loginVo.username", limit = 5, period = 60, message = "登录失败次数过多，请1分钟后再试")
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

    @Operation(summary = "注册")
    @PostMapping("/register")
    @RateLimit(prefix = "limit:register:", limitType = RateLimit.LimitType.IP, limit = 3, period = 60, message = "注册太频繁，请1分钟后再试")
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

    @Operation(summary = "发送邮箱验证码")
    @GetMapping("sendCode")
    @RateLimit(prefix = "limit:email:", key = "#email", limit = 1, period = 60, message = "验证码发送太频繁，请60秒后再试")
    public Result sendCode(@RequestParam String email) {
        if (sysUserService.getByEmail(email) != null) {
            return Result.ok("该邮箱已被注册");
        }
        log.info("发送验证码到邮箱: {}", email);
        String verifyCode = VCodeUtil.verifyCode(6);
        redisTemplate.opsForValue().set(email + "verify", verifyCode, 5 * 60, TimeUnit.SECONDS);
        log.info("验证码: {}", verifyCode);
        // 异步发送邮件（通过 RabbitMQ）
        messageProducer.sendVerifyCodeEmail(email, verifyCode);
        return Result.ok();
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserVo> info(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        if (userId == null || userId.isEmpty()) {
            return Result.build(null, ResultCodeEnum.LOGIN_AUTH);
        }
        UserVo sysUser = sysUserService.getUserInfo(Long.parseLong(userId));
        // 将 DB 中存储的相对 Key 拼接为完整 URL 后返回给前端
        if (sysUser != null) {
            sysUser.setHeadUrl(fileService.getFullUrl(sysUser.getHeadUrl()));
        }
        return Result.ok(sysUser);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        redisTemplate.delete(userId);
        return Result.ok();
    }

    @Operation(summary = "保存头像")
    @PostMapping("/savePhoto/{id}")
    public Result saveAvatar(@RequestBody JsonNode jsonNode, @PathVariable("id") Long id) {
        String url = jsonNode.get("url").asText();
        url = URLDecoder.decode(url);
        // 如果前端传来的是完整 URL（如不同环境下的历史数据），截取成相对 Key 再存到 DB
        // 正常情况下 upload 接口已返回 Key，此处兴起兼容作用
        if (url.startsWith("http://") || url.startsWith("https://")) {
            // 截取域名后的部分作为 Key
            int schemeEnd = url.indexOf("://");
            int slashAfterHost = url.indexOf('/', schemeEnd + 3);
            if (slashAfterHost != -1) {
                url = url.substring(slashAfterHost + 1);
            }
        }
        sysUserService.saveAvatar(url, id);
        return Result.ok();
    }

    @Operation(summary = "修改密码")
    @PostMapping("/modify/password")
    public Result modifyPass(@RequestParam("pass") String pass,
            HttpServletRequest request) {
        String userId = JwtHelper.getUserId(request.getHeader("Authorization"));
        sysUserService.modifyPass(pass, userId);
        return Result.ok();
    }

    @Operation(summary = "发送找回密码验证码")
    @GetMapping("/sendResetCode")
    @RateLimit(prefix = "limit:resetEmail:", key = "#email", limit = 1, period = 60, message = "验证码发送太频繁，请60秒后再试")
    public Result sendResetCode(@RequestParam String email) {
        if (sysUserService.getByEmail(email) == null) {
            return Result.build(null, ResultCodeEnum.NO_USER);
        }
        String verifyCode = VCodeUtil.verifyCode(6);
        redisTemplate.opsForValue().set(email + "reset", verifyCode, 5 * 60, TimeUnit.SECONDS);
        messageProducer.sendResetPasswordEmail(email, verifyCode);
        return Result.ok();
    }

    @Operation(summary = "找回密码（通过邮箱验证码重置）")
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestParam String email,
                                @RequestParam String code,
                                @RequestParam String pass) {
        if (sysUserService.getByEmail(email) == null) {
            return Result.build(null, ResultCodeEnum.NO_USER);
        }
        String cachedCode = redisTemplate.opsForValue().get(email + "reset");
        if (cachedCode == null || !cachedCode.equals(code)) {
            return Result.verifyError();
        }
        sysUserService.resetPasswordByEmail(email, pass);
        redisTemplate.delete(email + "reset");
        return Result.ok();
    }

    @Operation(summary = "修改个人信息")
    @PostMapping("/update/profile")
    public Result updateProfile(@RequestBody UserVo userVo) {
        sysUserService.updateProfile(userVo);
        return Result.ok();
    }
}
