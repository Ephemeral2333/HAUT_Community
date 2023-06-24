package com.liyh.system.service.serviceImpl;

import com.liyh.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @Author LiYH
 * @Description 邮件发送服务实现类
 * @Date 2023/6/5 23:33
 **/
@Service
public class EmailSerciveImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("LiYH_0703@foxmail.com");
        message.setTo(email);
        message.setSubject("HAUT社区交流平台验证码");
        message.setText("您好，欢迎注册HAUT社区交流平台，您的验证码是：" + code + "，请勿泄露给他人。验证码5分钟内有效哦。");
        mailSender.send(message);
    }

    @Override
    public void sendPostResultEmail(String email, String result, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("LiYH_0703@foxmail.com");
        message.setTo(email);
        message.setSubject("HAUT社区交流平台每日一句投稿结果");
        if ("pass".equals(result)) {
            message.setText("您好，您投稿的每日一句：" + content + "。已通过审核，您可以在主页每日一句栏目中找到他，十分感谢您的投稿。");
        } else {
            message.setText("您好,很抱歉，您投稿的每日一句：" + content + "。未通过审核，您可以尝试更换其他投稿内容，十分感谢您的投稿。");
        }
        mailSender.send(message);
    }
}
