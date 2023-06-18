package com.liyh.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author LiYH
 * @Description 存储文件
 * @Date 2023/6/18 15:06
 **/
public interface FileService {
    String saveImage(MultipartFile file) throws IOException;
}
