package com.liyh.model.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liyh.model.base.BaseEntity;
import lombok.Data;


@Data
@TableName("sys_role")
public class SysRole extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@TableField("name")
	private String name;

	@TableField("code")
	private String code;

	@TableField("remark")
	private String remark;

}

