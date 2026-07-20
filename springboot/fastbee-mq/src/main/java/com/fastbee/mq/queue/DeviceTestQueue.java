package com.fastbee.mq.queue;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.SneakyThrows;

import com.fastbee.common.extend.core.domin.mq.DeviceTestReportBo;

/**
 * @author bill
 */
public class DeviceTestQueue {


    private static final LinkedBlockingQueue<DeviceTestReportBo> queue = new LinkedBlockingQueue<>();

    /*元素加入队列,最后*/
    public static void offer(DeviceTestReportBo dto){
        queue.offer(dto);
    }
    /*取出队列元素 先进先出*/
    @SneakyThrows
    public static DeviceTestReportBo take(){
        return queue.take();
    }
}
