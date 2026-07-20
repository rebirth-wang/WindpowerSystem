package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 产品对象 iot_product
 *
 * @author zhuangpeng.li
 * @date 2024-12-20
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductVO", description = "产品 iot_product")
@Data
public class ProductVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 协议编号 */
    @Excel(name = "协议编号")
    @ApiModelProperty("协议编号")
    private String protocolCode;

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

    /** 是否启用授权码（0-否，1-是） */
    @ApiModelProperty("是否启用授权码")
    @Excel(name = "是否启用授权码")
    private Integer isAuthorize;

    /** mqtt账号 */
    @Excel(name = "认证账号")
    @ApiModelProperty("认证账号")
    private String account;

    /** mqtt密码 */
    @Excel(name = "认证密码")
    @ApiModelProperty("认证密码")
    private String authPassword;

    /** 产品秘钥 */
    @Excel(name = "产品认证秘钥")
    @ApiModelProperty("产品认证秘钥")
    private String secret;

    /** 状态（1-未发布，2-已发布） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Integer status;

    /** 物模型JSON（属性、功能、事件） */
    @ApiModelProperty("物模型JSON")
    @Excel(name = "物模型JSON")
    private String thingsModelsJson;

    /** 设备类型（1-直连设备、2-网关设备、3-监控设备） */
    @ApiModelProperty("设备类型")
    @Excel(name = "设备类型")
    private Integer deviceType;

    /** 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他） */
    @ApiModelProperty("联网方式")
    @Excel(name = "联网方式")
    private Integer networkMethod;

    /** 认证方式（1-简单认证、2-加密认证、3-简单+加密） */
    @ApiModelProperty("认证方式")
    @Excel(name = "认证方式")
    private Integer vertificateMethod;

    /** 图片地址 */
    @Excel(name = "图片地址")
    @ApiModelProperty("图片地址")
    private String imgUrl;

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

    /** 产品支持的传输协议 */
    @Excel(name = "产品支持的传输协议")
    @ApiModelProperty("产品支持的传输协议")
    private String transport;

    /** 定位方式(1=ip自动定位，2=设备定位，3=自定义) */
    @Excel(name = "定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    @ApiModelProperty("定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    private Integer locationWay;

    /** 产品关联的组态id */
    @Excel(name = "产品关联的组态id")
    @ApiModelProperty("产品关联的组态id")
    private String guid;

    /**采集点模板id*/
    @ApiModelProperty("采集点模板id")
    private Long templateId;

    @ApiModelProperty("是否显示上级")
    private Boolean showSenior;

    @ApiModelProperty("机构ID")
    private Long deptId;

    @ApiModelProperty("是否属于本机构 1-是，0-否")
    private Integer isOwner;

    private Boolean isAdmin;

    /**
     * 组态主键id
     */
    @ApiModelProperty("关联组态id")
    private Long scadaId;

    /**
     * 固件类型(1-二进制包升级，2-http升级)
     */
    @ApiModelProperty("产品固件类型")
    private Integer firmwareType;

    /**
     * 面板启用(0-停用，1-启用)
     */
    @ApiModelProperty("面板启用")
    private Integer panelEnable;

    /**
     * 是否可选
     */
    private Boolean canSelect;

    /**
     * 从机id
     */
    private Integer slaveId;

    /**
     * 拖拽面板json数据
     */
    @ApiModelProperty("拖拽面板json数据")
    private String panelModelsJson;

    private List<SubProductImportVO> subProductList;

}
