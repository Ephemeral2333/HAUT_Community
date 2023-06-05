package com.liyh.model.system;

import com.liyh.model.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "菜单")
public class SysMenu extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	private String name;

	private Integer parentId;

	private String principal;

	private Integer sort;

	private boolean disabled;

	private List<SysMenu> children;

	private SysMenu higherDeptOptions;
}

