package com.fastbee.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色和部门关联 SysRoleDeptVO
 *
 * @author ruoyi
 */
@Data
@ApiModel(value = "SysRoleDeptVO", description = "角色和部门关联VO类")
public class SysRoleDeptVO
{
    /** 角色ID */
    @ApiModelProperty("角色ID")
    private Long roleId;

    /** 部门ID */
    @ApiModelProperty("部门ID")
    private Long deptId;

    private Long deptUserId;

}
