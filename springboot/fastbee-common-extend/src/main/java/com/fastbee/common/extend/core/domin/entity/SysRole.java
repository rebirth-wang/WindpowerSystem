package com.fastbee.common.extend.core.domin.entity;

import java.util.Date;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 角色表 sys_role
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysRole", description = "角色信息 sys_role")
@Data
@TableName("sys_role")
public class SysRole extends PageEntity {

    /**
     * 角色ID
     */
    @TableId(value = "role_id", type = IdType.AUTO)
    @ApiModelProperty("角色ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sys_role.role_id=sys_user_role.role_id"
    )
    private Long roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    /**
     * 显示顺序
     */
    @ApiModelProperty("显示顺序")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    @ApiModelProperty("数据范围")
    @AiSemanticField(
            semanticName = "数据范围",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "data_scope",
            valueMappings = {"：全部数据权限=1", "：自定数据权限=2", "：本部门数据权限=3", "：本部门及以下数据权限=4"}
    )
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    @ApiModelProperty("菜单树选择项是否关联显示")
    private Integer menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    @ApiModelProperty("部门树选择项是否关联显示")
    private Integer deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty("角色状态")
    @AiSemanticField(
            semanticName = "角色状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_normal_disable",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    private boolean flag = false;
    @Excel(name = "菜单组")
    @ApiModelProperty("菜单组")
    private Long[] menuIds;
    @Excel(name = "部门组")
    @ApiModelProperty("部门组")
    private Long[] deptIds;
    @Excel(name = "角色菜单权限")
    @ApiModelProperty("角色菜单权限")
    private Set<String> permissions;

    private Long deptId;
    private String deptName;
    private Boolean showChild;
    private Boolean canEditRole;
    private Boolean manager;

    public SysRole() {

    }

    @JsonGetter("menuCheckStrictly")
    public Boolean getMenuCheckStrictlyJson() {
        return this.menuCheckStrictly != null && this.menuCheckStrictly == 1;
    }

    @JsonGetter("deptCheckStrictly")
    public Boolean getDeptCheckStrictlyJson() {
        return this.deptCheckStrictly != null && this.deptCheckStrictly == 1;
    }

    @JsonProperty("menuCheckStrictly")
    public void setMenuCheckStrictlyJson(Boolean value) {
        this.menuCheckStrictly = (value != null && value) ? 1 : 0;
    }

    @JsonProperty("deptCheckStrictly")
    public void setDeptCheckStrictlyJson(Boolean value) {
        this.deptCheckStrictly = (value != null && value) ? 1 : 0;
    }

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }
}
