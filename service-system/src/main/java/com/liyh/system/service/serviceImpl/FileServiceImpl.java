package com.liyh.system.service.serviceImpl;

import com.alibaba.fastjson2.JSONObject;
import com.liyh.common.utils.FileUtil;
import com.liyh.system.service.FileService;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

/**
 * @Author LiYH
 * @Description 文件存储服务
 * @Date 2023/6/18 15:07
 **/
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${qiniu.access-key}")
    private String accessKey;

    @Value("${qiniu.secret-key}")
    private String secretKey;

    @Value("${qiniu.bucket}")
    private String bucket;

    @Value("${qiniu.domain}")
    private String domain;

    private Auth auth;
    private UploadManager uploadManager;

    @PostConstruct
    public void init() {
        // 初始化七牛云认证和上传管理器
        this.auth = Auth.create(accessKey, secretKey);
        Configuration cfg = new Configuration(Zone.autoZone());
        this.uploadManager = new UploadManager(cfg);
        log.info("七牛云服务初始化完成, bucket: {}", bucket);
    }

    @Override
    public String getUpToken() {
        return auth.uploadToken(bucket);
    }

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                log.warn("文件名为空");
                return null;
            }

            int dotPos = originalFilename.lastIndexOf(".");
            if (dotPos < 0) {
                log.warn("文件没有扩展名: {}", originalFilename);
                return null;
            }

            String fileExt = originalFilename.substring(dotPos + 1).toLowerCase();
            // 判断是否是合法的文件后缀
            if (!FileUtil.isFileAllowed(fileExt)) {
                log.warn("不支持的文件类型: {}", fileExt);
                return null;
            }

            // 生成唯一文件名，存储在 community/ 目录下
            String key = "community/" + UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            // 调用 put 方法上传
            Response res = uploadManager.put(file.getBytes(), key, getUpToken());
            // 打印返回的信息
            if (res.isOK() && res.isJson()) {
                String returnedKey = (String) JSONObject.parseObject(res.bodyString()).get("key");
                log.info("文件上传成功, key: {}", returnedKey);
                return getFullUrl(returnedKey);
            } else {
                log.error("七牛云上传失败: {}", res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            log.error("七牛云上传异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String getFullUrl(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        // 如果已经是完整 URL（兼容老数据），直接返回
        if (key.startsWith("http://") || key.startsWith("https://")) {
            return key;
        }
        return domain + key;
    }
}
