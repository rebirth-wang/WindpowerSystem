package com.fastbee.onvif.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * onvif平台对象 onvif_platform
 *
 * @author fastbee
 * @date 2026-01-06
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OnvifPlatform", description = "onvif平台 onvif_platform")
@Data
@TableName("onvif_platform" )
public class OnvifPlatform extends PageEntity {
    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 通道id */
    @ApiModelProperty("通道id")
    private Long channelId;

    /** 平台id */
    @ApiModelProperty("平台id")
    private String platformId;

    /** 目录id */
    @ApiModelProperty("目录id")
    private String catalogId;

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
}
