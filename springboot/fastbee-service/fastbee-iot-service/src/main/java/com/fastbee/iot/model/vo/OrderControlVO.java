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
 * 指令权限控制对象 order_control
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "OrderControlVO", description = "指令权限控制 order_control")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderControlVO extends PageEntity {

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 逗号分隔 */
    @Excel(name = "逗号分隔")
    @ApiModelProperty("逗号分隔")
    private String selectOrder;

    /** 是否生效 0-否 1-是 */
    @Excel(name = "是否生效 0-否 1-是")
    @ApiModelProperty("是否生效 0-否 1-是")
    private Integer status;

    /** 被限制的用户id */
    @Excel(name = "被限制的用户id")
    @ApiModelProperty("被限制的用户id")
    private Long userId;

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 可操作次数 */
    @Excel(name = "可操作次数")
    @ApiModelProperty("可操作次数")
    private Long count;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开始时间")
    @Excel(name = "开始时间")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("结束时间")
    @Excel(name = "结束时间")
    private Date endTime;

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

    /** 文件路径 */
    @Excel(name = "文件路径")
    @ApiModelProperty("文件路径")
    private String filePath;

    /** 图片路径 */
    @Excel(name = "图片路径")
    @ApiModelProperty("图片路径")
    private String imgUrl;

    /** 模板名称 */
    @Excel(name = "模板名称")
    @ApiModelProperty("模板名称")
    private String modelNames;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 用户名称 */
    @Excel(name = "用户名称")
    @ApiModelProperty("用户名称")
    private String userName;

}
