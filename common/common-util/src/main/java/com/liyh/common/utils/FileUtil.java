package com.liyh.common.utils;

/**
 * @Author LiYH
 * @Description 七牛云文件上传工具类
 * @Date 15:02 2023/6/18
 **/

public class FileUtil {
    // 图片允许的后缀扩展名
    public static String[] IMAGE_FILE_EXTD = new String[]{"png", "bmp", "jpg", "jpeg", "pdf"};

    public static boolean isFileAllowed(String fileName) {
        for (String ext : IMAGE_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
}
