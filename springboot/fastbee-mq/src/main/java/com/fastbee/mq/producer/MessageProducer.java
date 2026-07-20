package com.fastbee.mq.producer;

import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.common.extend.core.domin.mq.DeviceTestReportBo;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.core.domin.mq.ota.OtaUpgradeBo;
import com.fastbee.iot.model.modbus.ModbusPollJob;
import com.fastbee.mq.queue.*;

/**
 *设备消息生产者 ,设备的消息发送通道
 * @author bill
 */
public class MessageProducer {

    /*发送设备获取属性消息到队列*/
    public static void sendPropFetch(ModbusPollJob deviceJob){
        DevicePropFetchQueue.offer(deviceJob);
    }
    /*发送设备服务下发消息到队列*/
    public static void sendFunctionInvoke(MQSendMessageBo bo){
        FunctionInvokeQueue.offer(bo);
    }
    /*发送设备上报消息到队列*/
    public static void sendPublishMsg(DeviceReportBo bo){
        DeviceReportQueue.offer(bo);
    }
    public static void sendOtherMsg(DeviceReportBo bo){
        DeviceOtherQueue.offer(bo);
    }

    public static void sendStatusMsg(DeviceStatusBo bo){
        DeviceStatusQueue.offer(bo);
    }
    /**
     * 设备调试通道
     * @param bo
     */
    public static void sendDeviceTestMsg(DeviceTestReportBo bo){
        DeviceTestQueue.offer(bo);
    }

    /*发送OTA消息到队列*/
    public static void sendOtaUpgradeMsg(OtaUpgradeBo bo) {
        OtaUpgradeQueue.offer(bo);
    }
    /*发送设备回复消息到队列*/
    public static void sendDeviceReplyMsg(DeviceReportBo bo) {
        DeviceReplyQueue.offer(bo);
    }

}
