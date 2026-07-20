package com.fastbee.iot.model.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 产品固件对象 iot_firmware
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "FirmwareVO", description = "产品固件 iot_firmware")
@Data
public class FirmwareVO{

    /** 固件ID */
    @Excel(name = "固件ID")
    @ApiModelProperty("固件ID")
    private Long firmwareId;

    /** 固件名称 */
    @Excel(name = "固件名称")
    @ApiModelProperty("固件名称")
    private String firmwareName;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

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

    /** 是否最新版本（0-否，1-是） */
    @ApiModelProperty("是否最新版本")
    @Excel(name = "是否最新版本")
    private Integer isLatest;

    /** 固件版本 */
    @Excel(name = "固件版本")
    @ApiModelProperty("固件版本")
    private BigDecimal version;

    /** 文件路径 */
    @Excel(name = "文件路径")
    @ApiModelProperty("文件路径")
    private String filePath;

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


}
