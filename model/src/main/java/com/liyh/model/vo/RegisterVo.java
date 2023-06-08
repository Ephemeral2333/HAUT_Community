package com.liyh.model.vo;

import lombok.Data;

/**
 * @Author LiYH
 * @Description TODO
 * @Date 2023/6/5 22:18
 **/
@Data
public class RegisterVo {
    private String username;

    private String nickname;

    private String pass;

    private String checkPass;

    private String email;

    private String code;
}
