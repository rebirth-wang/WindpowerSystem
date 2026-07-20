package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 产品分类对象 iot_category
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CategoryVO", description = "产品分类 iot_category")
@Data
public class CategoryVO extends PageEntity {

    /** 产品分类ID */
    @Excel(name = "产品分类ID")
    @ApiModelProperty("产品分类ID")
    private Long categoryId;

    /** 产品分类名称 */
    @Excel(name = "产品分类名称")
    @ApiModelProperty("产品分类名称")
    private String categoryName;

    /** 租户ID */
    @Excel(name = "租户ID")
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    @Excel(name = "是否系统通用")
    private Integer isSys;

    /** 父级ID */
    @Excel(name = "父级ID")
    @ApiModelProperty("父级ID")
    private Long parentId;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    @ApiModelProperty("显示顺序")
    private Integer orderNum;

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

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否显示上级品类")
    private Boolean showSenior;

    @ApiModelProperty("是否是总账号")
    private Boolean admin;

    @ApiModelProperty("机构ID")
    private Long deptId;

}
