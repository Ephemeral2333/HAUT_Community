package com.liyh.system.exception;

import com.liyh.common.result.Result;
import com.liyh.common.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.AccessDeniedException;


/**
 * 全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @Author LiYH
     * @Description 捕获权限异常
     * @Date 16:30 2023/5/10
     * @Param [e]
     * @return com.liyh.common.result.Result
     **/
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException {
        return Result.fail().code(ResultCodeEnum.PERMISSION.getCode()).message("没有权限");
    }
}