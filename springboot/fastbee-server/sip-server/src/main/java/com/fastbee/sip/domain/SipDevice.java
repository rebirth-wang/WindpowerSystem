package com.fastbee.sip.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 监控设备对象 sip_device
 *
 * @author zhuangpeng.li
 * @date 2024-11-20
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SipDevice", description = "监控设备 sip_device")
@Data
@TableName("sip_device" )
public class SipDevice extends PageEntity{
    /** 设备ID */
    @TableId(value = "device_id", type = IdType.AUTO)
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sip_device.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sip_device.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备SipID */
    @ApiModelProperty("设备SipID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sip_device.device_sip_id=sip_device_channel.device_sip_id"
    )
    private String deviceSipId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 厂商名称 */
    @ApiModelProperty("厂商名称")
    private String manufacturer;

    /** 产品型号 */
    @ApiModelProperty("产品型号")
    private String model;

    /** 固件版本 */
    @ApiModelProperty("固件版本")
    private String firmware;

    /** 传输模式 */
    @ApiModelProperty("传输模式")
    private String transport;

    /** 流模式 */
    @ApiModelProperty("流模式")
    private String streamMode;

    /** 在线状态 */
    @ApiModelProperty("在线状态")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private String onlineStatus;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("注册时间")
    private Date registerTime;

    /** 最后上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("最后上线时间")
    private Date lastConnectTime;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("激活时间")
    private Date activeTime;

    /** 设备入网IP */
    @ApiModelProperty("设备入网IP")
    private String ip;

    /** 设备接入端口号 */
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 设备地址 */
    @ApiModelProperty("设备地址")
    private String hostAddress;

    /** 接入sip服务ID */
    @ApiModelProperty("接入sip服务ID")
    private String serverId;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

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

    @TableField(exist = false)
    private boolean broadcastPushAfterAck;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }


    public String getHostAndPort() {
        return getIp() + ":" + getPort();
    }
}
