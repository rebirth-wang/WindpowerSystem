package com.fastbee.iot.domain;

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
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备扩展参数值对象 iot_device_ext_param_value
 *
 * @author fastbee
 * @date 2026-03-18
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceExtParamValue", description = "设备扩展参数值 iot_device_ext_param_value")
@Data
@TableName("iot_device_ext_param_value" )
public class DeviceExtParamValue extends PageEntity {
    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键ID")
    private Long id;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_ext_param_value.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 参数ID */
    @ApiModelProperty("参数ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_ext_param_value.param_id=iot_product_ext_param.param_id"
    )
    private Long paramId;

    /** 参数值 */
    @ApiModelProperty("参数值")
    private String paramValue;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;


}
