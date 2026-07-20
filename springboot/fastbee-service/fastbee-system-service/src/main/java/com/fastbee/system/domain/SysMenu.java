package com.fastbee.system.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 菜单权限表 sys_menu
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysMenu", description = "菜单权限表 sys_menu")
@Data
@TableName("sys_menu")
public class SysMenu extends PageEntity {
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId(value = "menu_id", type = IdType.AUTO)
    @ApiModelProperty("菜单ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sys_menu.menu_id=sys_role_menu.menu_id"
    )
    private Long menuId;

    /** 菜单名称 */
    @ApiModelProperty("菜单名称")
    private String menuName;

    /** 父菜单ID */
    @ApiModelProperty("父菜单ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long parentId;

    /** 显示顺序 */
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

    /** 路由地址 */
    @ApiModelProperty("路由地址")
    private String path;

    /** 组件路径 */
    @ApiModelProperty("组件路径")
    private String component;

    /**  */
    @ApiModelProperty("路由参数")
    private String queryParam;

    /** 是否为外链（0是 1否） */
    @ApiModelProperty("是否为外链")
    @AiSemanticField(
            semanticName = "是否为外链",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    @ApiModelProperty("是否缓存")
    @AiSemanticField(
            semanticName = "是否缓存",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer isCache;

    /** 菜单类型（M目录 C菜单 F按钮） */
    @ApiModelProperty("菜单类型")
    @AiSemanticField(
            semanticName = "菜单类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "menu_type",
            valueMappings = {"目录=M", "菜单=C", "按钮=F"}
    )
    private String menuType;

    /** 菜单状态（0显示 1隐藏） */
    @ApiModelProperty("菜单状态")
    @AiSemanticField(
            semanticName = "菜单状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_show_hide"
    )
    private String visible;

    /** 菜单状态（0正常 1停用） */
    @ApiModelProperty("菜单状态")
    @AiSemanticField(
            semanticName = "菜单状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_normal_disable",
            valueMappings = {"正常=0", "停用=1"},
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 权限标识 */
    @ApiModelProperty("权限标识")
    private String perms;

    /** 菜单图标 */
    @ApiModelProperty("菜单图标")
    private String icon;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("打开方式")
    @AiSemanticField(
            semanticName = "打开方式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_menu_target",
            valueMappings = {"页签=0", "新窗口=1"}
    )
    private Integer target;

    /** 子部门 */
    @TableField(exist = false)
    @ApiModelProperty("子菜单")
    private List<SysMenu> children = new ArrayList<SysMenu>();

    @Excel(name = "父菜单名称")
    @TableField(exist = false)
    @ApiModelProperty("父菜单名称")
    private String parentName;

    @TableField(exist = false)
    private Long deptId;

    @Deprecated
    @TableField(exist = false)
    private String language;

    /** 菜单名称 */
    @TableField(exist = false)
    @ApiModelProperty("中文菜单名称")
    private String menuName_zh_CN;

    /** 菜单名称 */
    @TableField(exist = false)
    @ApiModelProperty("英文菜单名称")
    private String menuName_en_US;

}
