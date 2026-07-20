package com.fastbee.mq.queue;

import java.util.concurrent.DelayQueue;

import lombok.extern.slf4j.Slf4j;

import com.fastbee.common.extend.core.domin.mq.ota.OtaUpgradeDelayTask;

/**
 * OTA延迟升级队列
 *
 * @author gsb
 * @date 2022/10/26 10:51
 */
@Slf4j
public class DelayUpgradeQueue {

    /**
     * 使用springboot的 DelayQueue实现延迟队列(OTA对单个设备延迟升级，提高升级容错率)
     */
    private static DelayQueue<OtaUpgradeDelayTask> queue = new DelayQueue<>();

    public static void offerTask(OtaUpgradeDelayTask task) {
        try {
            queue.offer(task);
        } catch (Exception e) {
            log.error("OTA任务推送异常", e);
        }
    }

    public static OtaUpgradeDelayTask task() {
        try {
            return queue.take();
        } catch (Exception exception) {
            log.error("=>OTA任务获取异常");
            return null;
        }
    }
}
