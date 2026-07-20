package com.fastbee.onvif.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * onvif平台对象 onvif_platform
 *
 * @author fastbee
 * @date 2026-01-06
 */

@ApiModel(value = "OnvifPlatformVO", description = "onvif平台 onvif_platform")
@Data
public class OnvifPlatformVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 通道id */
    @Excel(name = "通道id")
    @ApiModelProperty("通道id")
    private Long channelId;

    /** 平台id */
    @Excel(name = "平台id")
    @ApiModelProperty("平台id")
    private String platformId;

    /** 目录id */
    @Excel(name = "目录id")
    @ApiModelProperty("目录id")
    private String catalogId;

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


    /** 自定义代码区域 END**/
}
