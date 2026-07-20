package com.fastbee.system.domain.vo;

import java.util.Date;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 角色信息对象 sys_role
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@ApiModel(value = "SysRoleVO", description = "角色信息 sys_role")
@Data
public class SysRoleVO {
    /** 代码生成区域 可直接覆盖**/
    /**
     * 角色ID
     */
    @Excel(name = "角色ID")
    @ApiModelProperty("角色ID")
    private Long roleId;

    /**
     * 角色名称
     */
    @Excel(name = "角色名称")
    @ApiModelProperty("角色名称")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @Excel(name = "角色权限字符串")
    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    /**
     * 显示顺序
     */
    @Excel(name = "显示顺序")
    @ApiModelProperty("显示顺序")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @ApiModelProperty("数据范围")
    @Excel(name = "数据范围")
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @Excel(name = "菜单树选择项是否关联显示")
    @ApiModelProperty("菜单树选择项是否关联显示")
    private Integer menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @Excel(name = "部门树选择项是否关联显示")
    @ApiModelProperty("部门树选择项是否关联显示")
    private Integer deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty("角色状态")
    @Excel(name = "角色状态")
    private Integer status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

    /**
     * 创建者
     */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @TableField(exist = false)
    private boolean flag = false;
    @TableField(exist = false)
    @ApiModelProperty("菜单组")
    private Long[] menuIds;
    @TableField(exist = false)
    @ApiModelProperty("部门组")
    private Long[] deptIds;
    @TableField(exist = false)
    @ApiModelProperty("角色菜单权限")
    private Set<String> permissions;
    @TableField(exist = false)
    private Long deptId;
    @TableField(exist = false)
    private String deptName;
    @TableField(exist = false)
    private Boolean showChild;
    @TableField(exist = false)
    private Boolean canEditRole;
    @TableField(exist = false)
    private Boolean manager;

    public SysRoleVO() {
    }

    public SysRoleVO(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    public String getRoleName() {
        return roleName;
    }

    @NotBlank(message = "权限字符不能为空")
    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    public String getRoleKey() {
        return roleKey;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getRoleSort() {
        return roleSort;
    }
    /** 自定义代码区域 END**/
}
