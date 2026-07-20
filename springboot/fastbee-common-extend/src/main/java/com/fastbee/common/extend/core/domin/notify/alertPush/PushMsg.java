package com.fastbee.common.extend.core.domin.notify.alertPush;

import java.util.List;

import lombok.Data;

/**
 * 推送配置信息
 * @author bill
 */
@Data
public class PushMsg {

    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 告警id
     */
    private Long alertId;
    /**
     * 推送内容值
     */
    private AlertPushItem item;

    private List<String> values;
}
