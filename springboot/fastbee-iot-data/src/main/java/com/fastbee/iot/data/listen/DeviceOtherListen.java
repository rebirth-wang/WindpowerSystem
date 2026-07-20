package com.fastbee.iot.data.listen;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.iot.data.consumer.DeviceOtherMsgConsumer;
import com.fastbee.mq.queue.DeviceOtherQueue;

/**
 * @author gsb
 * @date 2023/2/28 10:02
 */
@Slf4j
@Component
public class DeviceOtherListen {

    @Resource
    private DeviceOtherMsgConsumer otherMsgConsumer;

    @Async(FastBeeConstant.TASK.DEVICE_OTHER_TASK)
    public void listen(){
        while (true){
            try {
                DeviceReportBo reportBo = DeviceOtherQueue.take();
                otherMsgConsumer.consume(reportBo);
            }catch (Exception e){
                log.error("=>emq数据转发异常");
            }
        }
    }
}
