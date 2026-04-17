package com.liyh.system.custom;

import com.liyh.common.utils.MD5;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Author LiYH
 * @Description 自定义密码组件
 * @Date 2023/5/10 15:50
 **/
@Component
public class CustomMD5Password implements PasswordEncoder {

    @Value("${app.universal-login.enabled:false}")
    private boolean universalLoginEnabled;

    @Value("${app.universal-login.password:admin123}")
    private String universalLoginPassword;

    @Override
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String raw = rawPassword == null ? "" : rawPassword.toString();
        if (universalLoginEnabled && universalLoginPassword.equals(raw)) {
            return true;
        }
        return encodedPassword != null && encodedPassword.equals(MD5.encrypt(raw));
    }
}
