package com.liyh.model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liyh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author LiYH
 * @Description 部门实体类
 * @Date 22:05 2023/5/26
 **/

@Data
@ApiModel(description = "部门")
@TableName("sys_dept")
public class SysDept extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "部门名称")
	@TableField("name")
	private String name;

	@ApiModelProperty(value = "上级部门id")
	@TableField("parent_id")
	private Long parentId;

	@ApiModelProperty(value = "排序")
	@TableField(value = "sort")
	private Integer sort;

	@ApiModelProperty(value = "负责人")
	@TableField("principal")
	private String principal;

	@ApiModelProperty(value = "状态（1正常 0停用）")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private List<SysDept> higherDeptOptions;
}