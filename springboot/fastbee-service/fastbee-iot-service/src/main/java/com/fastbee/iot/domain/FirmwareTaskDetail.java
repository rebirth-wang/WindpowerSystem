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
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 固件升级任务详细对象对象 iot_firmware_task_detail
 *
 * @author kerwincui
 * @date 2024-08-18
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FirmwareTaskDetail" , description = "固件升级任务详细对象 iot_firmware_task_detail" )
@Data
@TableName("iot_firmware_task_detail" )
public class FirmwareTaskDetail extends PageEntity {


    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键" )
    private Long id;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @AiSemanticField(
            semanticName = "task_id",
            semanticType = AiSemanticType.RELATION_KEY
    )
    private Long taskId;


    /** 设备编码 */
    @ApiModelProperty("设备编码" )
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_firmware_task_detail.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;


    /** 0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败 */
    @ApiModelProperty("0:等待升级 1:已发送设备 2:设备收到  3:升级成功 4:升级失败" )
    @AiSemanticField(
            semanticName = "0",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "firmware_upgrade_status"
    )
    private Integer upgradeStatus;


    /** 描述 */
    @ApiModelProperty("描述" )
    private String detailMsg;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date createTime;


    /** 消息ID */
    @ApiModelProperty("消息ID" )
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_firmware_task_detail.message_id=ai_chat_message.message_id"
    )
    private String messageId;


    /** $column.columnComment */
    @ApiModelProperty("${comment}" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date updateTime;

}
