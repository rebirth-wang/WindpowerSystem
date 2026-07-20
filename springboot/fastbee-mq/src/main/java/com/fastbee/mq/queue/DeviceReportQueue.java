package com.fastbee.mq.queue;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.SneakyThrows;

import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;

/**
 * @author gsb
 * @date 2022/10/10 10:13
 */
public class DeviceReportQueue {

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
