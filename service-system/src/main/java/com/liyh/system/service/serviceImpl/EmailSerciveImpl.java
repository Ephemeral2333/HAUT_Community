package com.liyh.system.service.serviceImpl;

import com.liyh.system.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @Author LiYH
 * @Description TODO
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
}
