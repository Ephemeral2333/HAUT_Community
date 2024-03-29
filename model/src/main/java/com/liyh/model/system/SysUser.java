package com.liyh.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.liyh.model.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "用户")
@TableName("sys_user")
public class SysUser extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户名")
	@TableField("username")
	private String username;

	@ApiModelProperty(value = "密码")
	@TableField("password")
	private String password;

	@ApiModelProperty(value = "姓名")
	@TableField("nickname")
	private String nickname;

	@ApiModelProperty(value = "邮箱")
	@TableField("email")
	private String email;

	@ApiModelProperty(value = "性别")
	@TableField("sex")
	private int sex;

	@ApiModelProperty(value = "头像地址")
	@TableField("head_url")
	private String headUrl;

	@ApiModelProperty(value = "部门id")
	@TableField("dept_id")
	private Long deptId;

	@ApiModelProperty(value = "描述")
	@TableField("description")
	private String description;

	@ApiModelProperty(value = "状态（1：正常 0：停用）")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private List<SysRole> roleList;

	@TableField(exist = false)
	private SysDept sysDept;

	@TableField(exist = false)
	private List<SysMenu> higherDeptOptions;

	@ApiModelProperty(value = "传输过来的部门ID")
	@TableField(exist = false)
	private Long parentId;

	@TableField(exist = false)
	private List<Long> roleIds;
}

