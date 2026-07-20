package com.fastbee.system.domain.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;
import com.fastbee.system.domain.SysMenu;

/**
 * 菜单权限对象 sys_menu
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@ApiModel(value = "SysMenuVO", description = "菜单权限 sys_menu")
@Data
public class SysMenuVO {
    /** 代码生成区域 可直接覆盖**/
    /** 菜单ID */
    @Excel(name = "菜单ID")
    @ApiModelProperty("菜单ID")
    private Long menuId;

    /** 菜单名称 */
    @Excel(name = "菜单名称")
    @ApiModelProperty("菜单名称")
    private String menuName;

    /** 父菜单ID */
    @Excel(name = "父菜单ID")
    @ApiModelProperty("父菜单ID")
    private Long parentId;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /** 路由地址 */
    @Excel(name = "路由地址")
    @ApiModelProperty("路由地址")
    private String path;

    /** 组件路径 */
    @Excel(name = "组件路径")
    @ApiModelProperty("组件路径")
    private String component;

    /**  */
    @Excel(name = "路由参数")
    @ApiModelProperty("路由参数")
    private String queryParam;

    /** 是否为外链（0是 1否） */
    @ApiModelProperty("是否为外链")
    @Excel(name = "是否为外链")
    private Integer isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    @ApiModelProperty("是否缓存")
    @Excel(name = "是否缓存")
    private Integer isCache;

    /** 菜单类型（M目录 C菜单 F按钮） */
    @ApiModelProperty("菜单类型")
    @Excel(name = "菜单类型")
    private String menuType;

    /** 菜单状态（0显示 1隐藏） */
    @ApiModelProperty("菜单状态")
    @Excel(name = "菜单状态")
    private String visible;

    /** 菜单状态（0正常 1停用） */
    @ApiModelProperty("菜单状态")
    @Excel(name = "菜单状态")
    private Integer status;

    /** 权限标识 */
    @Excel(name = "权限标识")
    @ApiModelProperty("权限标识")
    private String perms;

    /** 菜单图标 */
    @Excel(name = "菜单图标")
    @ApiModelProperty("菜单图标")
    private String icon;

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

    @ApiModelProperty("打开方式")
    private Integer target;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    @Excel(name = "父菜单名称")
    @ApiModelProperty("父菜单名称")
    private String parentName;

    @Excel(name = "子菜单")
    @ApiModelProperty("子菜单")
    private List<SysMenu> children = new ArrayList<SysMenu>();

    private Long deptId;

    @Deprecated
    private String language;

    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    public String getMenuName() {
        return menuName;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getOrderNum() {
        return orderNum;
    }

    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    public String getPath() {
        return path;
    }

    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    public String getComponent() {
        return component;
    }

    @NotBlank(message = "菜单类型不能为空")
    public String getMenuType() {
        return menuType;
    }

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms() {
        return perms;
    }
    /** 自定义代码区域 END**/
}
