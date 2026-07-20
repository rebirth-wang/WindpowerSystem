package com.fastbee.common.extend.core.domin.mq;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.enums.FunctionReplyStatus;
import com.fastbee.common.enums.ServerType;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.protocol.Message;

/**
 * 设备上行数据model
 *
 * @author bill
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceReport extends Message {

    /**
     * 设备编号
     */
    private String serialNumber;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 平台时间
     */
    private Date platformDate;
    /**
     * 消息id
     */
    private String messageId;

    /** 设备物模型值的集合 **/
    private List<ThingsModelSimpleItem> thingsModelSimpleItem;

    /**
     * 是否设备回复数据
     */
    private Boolean isReply = false;

    private String topic;
    /**
     * 原数据报文
     */
    private String sources;

    /**
     * 设备回复消息
     */
    private String replyMessage;
    /**
     * 设备回复状态
     */
    private FunctionReplyStatus status;
//    /**
//     * 从机地址
//     */
//    private Integer address;
    /**
     * 直连设备从机地址
     */
    private String directConnAddress;
    /**
     * 服务器类型
     */
    private ServerType serverType;

    private String protocolCode;

    private Long userId;
//    private String userName;
//    private String deviceName;

//    private SubDeviceBo subDeviceBo;

//    private GwDeviceBo gwDeviceBo;

    /**
     * 子设备集合
     */
    private List<SubDeviceBo> subDeviceBoList;

    private Message message;
}
