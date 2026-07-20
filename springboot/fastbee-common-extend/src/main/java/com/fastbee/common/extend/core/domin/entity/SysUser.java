package com.fastbee.common.extend.core.domin.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysUser", description = "用户信息 sys_user")
@Data
@TableName("sys_user")
public class SysUser extends PageEntity {
    /**
     * 用户ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty("用户ID")
    @Excel(name = "用户编号")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long userId;

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    @Excel(name = "部门编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sys_user.dept_id=sys_dept.dept_id;机构数据范围字段，查询时可按机构过滤",
            queryHint = "auto-data-scope"
    )
    private Long deptId;

    /**
     * 角色组
     */
    @TableField(exist = false)
    @ApiModelProperty("角色组")
    private Long[] roleIds;

    /**
     * 角色组（用于Excel导入导出的字符串格式）
     */
    @TableField(exist = false)
    @Excel(name = "角色编号")
    private String roleIdsStr;

    /**
     * 用户账号
     */
    @ApiModelProperty("用户账号")
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    @Excel(name = "用户昵称")
    private String nickName;

    /**
     * 用户类型（00系统用户）
     */
    @ApiModelProperty("用户类型")
    @Excel(name = "用户类型")
    @AiSemanticField(
            semanticName = "用户类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String userType;

    /**
     * 用户邮箱
     */
    @ApiModelProperty("用户邮箱")
    @Excel(name = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @ApiModelProperty("手机号码")
    @Excel(name = "手机号码")
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @ApiModelProperty("用户性别")
    @Excel(name = "用户性别")
    @AiSemanticField(
            semanticName = "用户性别",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_user_sex"
    )
    private String sex;

    /**
     * 头像地址
     */
    @ApiModelProperty("头像地址")
    @Excel(name = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    @Excel(name = "密码")
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty("帐号状态")
    @Excel(name = "帐号状态")
    @AiSemanticField(
            semanticName = "帐号状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_normal_disable",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer delFlag;

    /**
     * 最后登录IP
     */
    @ApiModelProperty("最后登录IP")
    @Excel(name = "最后登录IP")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后登录时间")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    @Excel(name = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    @Excel(name = "更新者")
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @Excel(name = "备注")
    private String remark;

    @TableField(exist = false)
    @ApiModelProperty("部门对象")
    private SysDept dept;
    /**
     * 角色对象
     */
    @TableField(exist = false)
    @ApiModelProperty("角色对象")
    private List<SysRole> roles;

    /**
     * 岗位组
     */
    @TableField(exist = false)
    @ApiModelProperty("岗位组")
    private Long[] postIds;
    /**
     * 角色ID
     */
    @TableField(exist = false)
    @ApiModelProperty("角色ID")
    private Long roleId;

    @TableField(exist = false)
    private Boolean manager;

    @TableField(exist = false)
    private String language;

    @TableField(exist = false)
    private String timeZone;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }
}
