package com.liyh.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.liyh.model.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "用户")
@TableName("sys_user")
public class SysUser extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@Schema(description = "用户名")
	@TableField("username")
	private String username;

	@Schema(description = "密码")
	@TableField("password")
	private String password;

	@Schema(description = "姓名")
	@TableField("nickname")
	private String nickname;

	@Schema(description = "邮箱")
	@TableField("email")
	private String email;

	@Schema(description = "性别")
	@TableField("sex")
	private int sex;

	@Schema(description = "头像地址")
	@TableField("head_url")
	private String headUrl;

	@Schema(description = "部门id")
	@TableField("dept_id")
	private Long deptId;

	@Schema(description = "描述")
	@TableField("description")
	private String description;

	@Schema(description = "状态（1：正常 0：停用）")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private List<SysRole> roleList;

	@TableField(exist = false)
	private SysDept sysDept;

	@TableField(exist = false)
	private List<SysMenu> higherDeptOptions;

	@Schema(description = "传输过来的部门ID")
	@TableField(exist = false)
	private Long parentId;

	@TableField(exist = false)
	private List<Long> roleIds;
}

