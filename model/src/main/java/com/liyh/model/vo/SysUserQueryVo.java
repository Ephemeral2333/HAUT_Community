//
//
package com.liyh.model.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用户查询实体
 * </p>
 */
@Data
public class SysUserQueryVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String username;
	private String email;
	private int status;
	private int deptId;
	private Pagination pagination;
}

