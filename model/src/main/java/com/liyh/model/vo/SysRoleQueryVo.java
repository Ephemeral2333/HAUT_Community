//
//
package com.liyh.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 角色查询实体
 * </p>
 *
 * @author qy
 * @since 2019-11-08
 */
@Data
public class SysRoleQueryVo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String code;
	private Pagination pagination;
}

