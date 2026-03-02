package com.liyh.model.vo.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 邮件消息实体
 *
 * @Author LiYH
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收件人邮箱
     */
    private String to;

    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;

    /**
     * 邮件类型：VERIFY_CODE(验证码), NOTIFY(通知), APPROVE(审核结果)
     */
    private EmailType type;

    /**
     * 邮件类型枚举
     */
    public enum EmailType {
        /**
         * 验证码
         */
        VERIFY_CODE,
        /**
         * 通知
         */
        NOTIFY,
        /**
         * 审核结果
         */
        APPROVE
    }
}
