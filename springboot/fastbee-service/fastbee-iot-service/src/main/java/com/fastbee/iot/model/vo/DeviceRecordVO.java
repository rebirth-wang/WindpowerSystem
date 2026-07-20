package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 设备记录对象 iot_device_record
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "DeviceRecordVO", description = "设备记录 iot_device_record")
@Data
public class DeviceRecordVO{

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 操作者机构id */
    @Excel(name = "操作者机构id")
    @ApiModelProperty("操作者机构id")
    private Long operateDeptId;

    /** 目标机构id */
    @Excel(name = "目标机构id")
    @ApiModelProperty("目标机构id")
    private Long targetDeptId;

    /** 产品id */
    @Excel(name = "产品id")
    @ApiModelProperty("产品id")
    private Long productId;

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 父id */
    @Excel(name = "父id")
    @ApiModelProperty("父id")
    private Long parentId;

    /** 设备记录类型（1-导入记录；2-回收记录；3-分配记录；4-分配详细记录） */
    @ApiModelProperty("设备记录类型")
    @Excel(name = "设备记录类型")
    private Integer type;

    /** 分配类型（1-选择分配；2-导入分配） */
    @ApiModelProperty("分配类型")
    @Excel(name = "分配类型")
    private Integer distributeType;

    /** 总数 */
    @Excel(name = "总数")
    @ApiModelProperty("总数")
    private Long total;

    /** 成功数量 */
    @Excel(name = "成功数量")
    @ApiModelProperty("成功数量")
    private Long successQuantity;

    /** 失败数量 */
    @Excel(name = "失败数量")
    @ApiModelProperty("失败数量")
    private Long failQuantity;

    /** 状态（0-失败；1-成功） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Integer status;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Integer delFlag;

    /** 操作者机构id */
    @Excel(name = "归属机构名称")
    private String operateDeptName;

    /** 目标机构id */
    @Excel(name = "目标机构名称")
    private String targetDeptName;

    /** 产品id */
    @Excel(name = "产品名称")
    private String productName;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 分配类型描述（1-选择分配；2-导入分配） */
    @Excel(name = "分配类型")
    private String distributeTypeDesc;

}
