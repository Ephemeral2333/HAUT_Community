package com.liyh.model.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liyh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author LiYH
 * @Description 部门实体类
 * @Date 22:05 2023/5/26
 **/

@Data
@Schema(description = "部门")
@TableName("sys_dept")
public class SysDept extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Schema(description = "部门名称")
	@TableField("name")
	private String name;

	@Schema(description = "上级部门id")
	@TableField("parent_id")
	private Long parentId;

	@Schema(description = "排序")
	@TableField(value = "sort")
	private Integer sort;

	@Schema(description = "负责人")
	@TableField("principal")
	private String principal;

	@Schema(description = "状态（1正常 0停用）")
	@TableField("status")
	private Integer status;

	@TableField(exist = false)
	private List<SysDept> higherDeptOptions;
}