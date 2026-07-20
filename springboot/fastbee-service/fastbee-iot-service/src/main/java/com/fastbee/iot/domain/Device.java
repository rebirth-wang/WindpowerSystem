package com.fastbee.iot.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;
import com.fastbee.iot.util.KingbaseJsonTypeHandler;

/**
 * 设备对象 iot_device
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Device", description = "设备 iot_device")
@Data
@TableName("iot_device" )
public class Device extends BaseEntity{
    /** 设备ID */
    @TableId(value = "device_id", type = IdType.AUTO)
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device.device_id=sip_device.device_id"
    )
    private Long deviceId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 子设备网关编号 */
    @ApiModelProperty("子设备网关编号")
    private String gwDevCode;

    /** 固件版本 */
    @ApiModelProperty("固件版本")
    private BigDecimal firmwareVersion;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    @AiSemanticField(
            semanticName = "设备状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_device_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 信号强度（
        信号极好4格[-55— 0]，
        信号好3格[-70— -55]，
        信号一般2格[-85— -70]，
        信号差1格[-100— -85]） */
    @ApiModelProperty("信号强度")
    @AiSemanticField(sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer rssi;

    /** 是否启用设备影子(0=禁用，1=启用) */
    @ApiModelProperty("是否启用设备影子(0=禁用，1=启用)")
    @AiSemanticField(
            semanticName = "是否启用设备影子",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "device_shadow"
    )
    private Integer isShadow;

    /** 定位方式(1=ip自动定位，2=设备定位，3=自定义) */
    @ApiModelProperty("定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    @AiSemanticField(
            semanticName = "定位方式",
            semanticType = AiSemanticType.ENUM
    )
    private Integer locationWay;

    /** 物模型值 */
    @TableField(typeHandler = KingbaseJsonTypeHandler.class)
    @ApiModelProperty("物模型值")
    @AiSemanticField(
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_location_way"
    )
    private String thingsModelValue;

    /** 设备所在地址 */
    @ApiModelProperty("设备所在地址")
    private String networkAddress;

    /** 设备入网IP */
    @ApiModelProperty("设备入网IP")
    private String networkIp;

    /** 设备经度 */
    @ApiModelProperty("设备经度")
    private BigDecimal longitude;

    /** 设备纬度 */
    @ApiModelProperty("设备纬度")
    private BigDecimal latitude;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("激活时间")
    private Date activeTime;

    /** 设备摘要，格式[{"name":"device"},{"chip":"esp8266"}] */
    @ApiModelProperty("设备摘要，格式[{\"name\":\"device\"},{\"chip\":\"esp8266\"}]")
    private String summary;

    /** 图片地址 */
    @ApiModelProperty("图片地址")
    private String imgUrl;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic(value = "0", delval = "NULL")
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 从机id */
    @ApiModelProperty("从机id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Integer slaveId;

    @ApiModelProperty("modbusTcp端口")
    private Integer devicePort;

    /** 设备主机ip */
    @ApiModelProperty("modbusTcp主机ip")
    private String deviceIp;

}
