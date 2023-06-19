package com.liyh.system.controller;

import com.liyh.common.result.Result;
import com.liyh.system.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/18 15:03
 **/
@ApiOperation(value = "文件管理", tags = "文件管理")
@RestController
@RequestMapping("/front/file")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.fail();
        }

        try {
            String fileUrl = fileService.saveImage(file);
            return Result.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.fail();
    }

    @GetMapping(value = "/getToken")
    public Result getToken() {
        String token = fileService.getUpToken();
        return Result.ok(token);
    }
}
