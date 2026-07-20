package com.fastbee.system.domain.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.core.domin.entity.SysDept;

/**
 * 机构对象 sys_dept
 *
 * @author zhuangpeng.li
 * @date 2024-12-25
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDeptVO", description = "机构 sys_dept")
@Data
public class SysDeptVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 机构id */
    @Excel(name = "机构id")
    @ApiModelProperty("机构id")
    private Long deptId;

    /** 机构系统账号ID */
    @Excel(name = "机构系统账号ID")
    @ApiModelProperty("机构系统账号ID")
    private Long deptUserId;

    /** 上级机构id */
    @Excel(name = "上级机构id")
    @ApiModelProperty("上级机构id")
    private Long parentId;

    /** 祖级列表 */
    @Excel(name = "祖级列表")
    @ApiModelProperty("祖级列表")
    private String ancestors;

    /** 机构名称 */
    @Excel(name = "机构名称")
    @ApiModelProperty("机构名称")
    private String deptName;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /** 联系人 */
    @Excel(name = "联系人")
    @ApiModelProperty("联系人")
    private String leader;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @ApiModelProperty("联系电话")
    private String phone;

    /** 机构状态（0正常 1停用） */
    @ApiModelProperty("机构状态")
    @Excel(name = "机构状态")
    private Integer status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 邮箱 */
    @Excel(name = "邮箱")
    @ApiModelProperty("邮箱")
    private String email;

    /** 机构类型 */
    @Excel(name = "机构类型")
    @ApiModelProperty("机构类型")
    private Integer deptType;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /** 父部门名称 */
    @ApiModelProperty("父部门名称")
    private String parentName;

    /** 子部门 */
    @ApiModelProperty("子部门")
    private List<SysDept> children = new ArrayList<SysDept>();

    /**
     * 系统账号名称
     */
    private String userName;

    /**
     * 系统账号密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 是否显示自己
     */
    private Boolean showOwner;

    /**
     * 管理员姓名
     */
    private String deptUserName;

    /** 邀请码 */
    private String invitationCode;

    /** 自定义代码区域 END**/
}
