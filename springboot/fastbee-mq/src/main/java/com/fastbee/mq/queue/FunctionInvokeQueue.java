package com.fastbee.mq.queue;

import java.util.concurrent.LinkedBlockingQueue;

import lombok.SneakyThrows;

import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;

/**
 * 服务下发队列 处理{@link MQSendMessageBo}
 * @author gsb
 * @date 2022/10/10 10:11
 */
public class FunctionInvokeQueue {
    private static final LinkedBlockingQueue<MQSendMessageBo> queue = new LinkedBlockingQueue<>();

    /*元素加入队列,最后*/
    public static void offer(MQSendMessageBo dto){
        queue.offer(dto);
    }
    /*取出队列元素 先进先出*/
    @SneakyThrows
    public static MQSendMessageBo take(){
        return queue.take();
    }
}
