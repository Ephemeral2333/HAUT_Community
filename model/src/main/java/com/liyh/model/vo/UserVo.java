package com.liyh.model.vo;

import com.liyh.model.system.SysDept;
import com.liyh.model.system.SysRole;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author LiYH
 * @Description 前台用户VO
 * @Date 2023/6/6 17:13
 **/
@Data
public class UserVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private String nickname;
    private int sex;
    private String headUrl;
    private String email;
    private String description;
    private List<SysRole> sysRoleList;
    private SysDept sysDept;
}
