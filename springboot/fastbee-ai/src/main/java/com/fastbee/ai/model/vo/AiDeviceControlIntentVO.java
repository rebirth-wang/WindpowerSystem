package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 设备控制意图解析结果。
 */
@Data
public class AiDeviceControlIntentVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 已解析出的设备候选。
     */
    private List<DeviceShortOutput> deviceCandidates = new ArrayList<>();

    /**
     * 已唯一定位的设备元数据。
     */
    private DeviceMetaData deviceMetaData;

    /**
     * 已解析出的物模型候选。
     */
    private List<ThingsModelValueItem> thingModelCandidates = new ArrayList<>();

    /**
     * 已唯一定位的物模型。
     */
    private ThingsModelValueItem thingModel;

    /**
     * 控制目标文本。
     */
    private String thingModelText;

    /**
     * 控制动作文本。
     */
    private String actionText;

    /**
     * 动作归一值。
     */
    private String actionValue;

    /**
     * 服务下发请求。
     */
    private InvokeReqDto invokeRequest;

    /**
     * 指令生成请求。
     */
    private MQSendMessageBo commandRequest;
}
