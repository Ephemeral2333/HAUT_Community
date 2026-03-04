package com.liyh.system.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author LiYH
 * @Description 存储文件
 * @Date 2023/6/18 15:06
 **/
public interface FileService {
    /**
     * 上传图片到七牛云，返回相对 Key（如 community/xxx.jpg），不含域名前缀
     */
    String saveImage(MultipartFile file) throws IOException;

    String getUpToken();

    /**
     * 将相对 Key 拼接为完整可访问 URL（domain + key）
     */
    String getFullUrl(String key);
}
