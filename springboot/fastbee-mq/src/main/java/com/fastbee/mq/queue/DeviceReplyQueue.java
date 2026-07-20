package com.fastbee.mq.queue;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.SneakyThrows;

import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;

/**
 * 设备消息回调队列 {@link DeviceReportBo}
 * @author gsb
 * @date 2022/10/10 10:15
 */
public class DeviceReplyQueue {
    private static final LinkedBlockingQueue<DeviceReportBo> queue = new LinkedBlockingQueue<>();

    /*元素加入队列,最后*/
    public static void offer(DeviceReportBo dto){
        queue.offer(dto);
    }
    /*取出队列元素 先进先出*/
    @SneakyThrows
    public static DeviceReportBo take(){
        return queue.take();
    }
}
