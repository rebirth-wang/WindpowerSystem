package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import com.fastbee.common.annotation.Excel;

/**
 * 设备分享对象 iot_device_share
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "DeviceShareVO", description = "设备分享 iot_device_share")
@Accessors(chain = true)
@Data
public class DeviceShareVO{

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 用户id */
    @Excel(name = "用户id")
    @ApiModelProperty("用户id")
    private Long userId;

    /** 手机 */
    @Excel(name = "手机")
    @ApiModelProperty("手机")
    private String phonenumber;

    /** 用户物模型权限，多个以英文逗号分隔 */
    @Excel(name = "用户物模型权限，多个以英文逗号分隔")
    @ApiModelProperty("用户物模型权限，多个以英文逗号分隔")
    private String perms;

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

    @Transient
    @ApiModelProperty("标识是否是拥有者")
    private Integer isOwner;

    @Transient
    private String userName;

}
