package com.liyh.system.service;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/5 23:32
 **/
public interface EmailService {
    /**
     * @return void
     * @Author LiYH
     * @Description 发送邮件
     * @Date 10:42 2023/6/20
     * @Param [email "收件人邮箱", code]
     **/
    void sendEmail(String email, String code);


    void sendPostResultEmail(String email, String result, String content);
}
