package com.fastbee.system.domain.vo;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.annotation.Excels;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysRole;
import com.fastbee.common.xss.Xss;

/**
 * 用户信息对象 sys_user
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@ApiModel(value = "SysUserVO", description = "用户信息 sys_user")
@Data
public class SysUserVO{
    /** 代码生成区域 可直接覆盖**/
    /** 用户ID */
    @Excel(name = "用户ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 部门ID */
    @Excel(name = "部门ID")
    @ApiModelProperty("部门ID")
    private Long deptId;

    /** 用户账号 */
    @Excel(name = "用户账号")
    @ApiModelProperty("用户账号")
    private String userName;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    @ApiModelProperty("用户昵称")
    private String nickName;

    /** 用户类型（00系统用户） */
    @ApiModelProperty("用户类型")
    @Excel(name = "用户类型")
    private String userType;

    /** 用户邮箱 */
    @Excel(name = "用户邮箱")
    @ApiModelProperty("用户邮箱")
    private String email;

    /** 手机号码 */
    @Excel(name = "手机号码")
    @ApiModelProperty("手机号码")
    private String phonenumber;

    /** 用户性别（0男 1女 2未知） */
    @ApiModelProperty("用户性别")
    @Excel(name = "用户性别")
    private String sex;

    /** 头像地址 */
    @Excel(name = "头像地址")
    @ApiModelProperty("头像地址")
    private String avatar;

    /** 密码 */
    @Excel(name = "密码")
    @ApiModelProperty("密码")
    private String password;

    /** 帐号状态（0正常 1停用） */
    @ApiModelProperty("帐号状态")
    @Excel(name = "帐号状态")
    private Integer status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private Integer delFlag;

    /** 最后登录IP */
    @Excel(name = "最后登录IP")
    @ApiModelProperty("最后登录IP")
    private String loginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后登录时间")
    @Excel(name = "最后登录时间")
    private Date loginDate;

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

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /** 部门对象 */
    @Excel(name = "部门对象")
    @ApiModelProperty("部门对象")
    @Excels({
            @Excel(name = "部门名称", targetAttr = "deptName", type = Excel.Type.EXPORT),
            @Excel(name = "部门负责人", targetAttr = "leader", type = Excel.Type.EXPORT)
    })
    private SysDept dept;
    /** 角色对象 */
    @Excel(name = "角色对象")
    @ApiModelProperty("角色对象")
    private List<SysRole> roles;
    /** 角色组 */
    @Excel(name = "角色组")
    @ApiModelProperty("角色组")
    private Long[] roleIds;
    /** 岗位组 */
    @Excel(name = "岗位组")
    @ApiModelProperty("岗位组")
    private Long[] postIds;
    /** 角色ID */
    @Excel(name = "角色ID")
    @ApiModelProperty("角色ID")
    private Long roleId;
    private Boolean showChild;
    private Boolean manager;
    private String language;
    private String timeZone;

    public SysUserVO() {
    }

    public SysUserVO(Long userId)
    {
        this.userId = userId;
    }
    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName()
    {
        return nickName;
    }
    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName()
    {
        return userName;
    }
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail()
    {
        return email;
    }
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber()
    {
        return phonenumber;
    }

    /** 自定义代码区域 END**/
}
