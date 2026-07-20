package com.fastbee.modbus.tcp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Component;

import com.fastbee.common.extend.core.domin.mq.message.ModbusDevice;

@Component
public class DeviceStatusCache {
    private static final ConcurrentMap<Long, Boolean> currentPollStatus = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Long, Boolean> lastDbStatus = new ConcurrentHashMap<>();

    public static boolean FIRST_LOAD = true;


    public static void markOnline(Long deviceId) {
        currentPollStatus.put(deviceId, true);
    }

    public static void markOffline(Long deviceId) {
        currentPollStatus.put(deviceId, false);
    }


    public static Map<Long, Integer> getChangedStatus() {
        Map<Long, Integer> changes = new HashMap<>();

        // 遍历本次轮询的所有设备
        currentPollStatus.forEach((deviceId, currentStatus) -> {
            Boolean lastStatus = lastDbStatus.get(deviceId);
            // 如果数据库中没有记录，或者状态发生变化
            if (lastStatus == null || lastStatus != currentStatus) {
                changes.put(deviceId, currentStatus ? 3 : 4);
            }
        });

        // 清空本次轮询状态，准备下一次轮询
        currentPollStatus.clear();
        return changes;
    }

    public static void init(List<ModbusDevice> devices) {
        if (FIRST_LOAD) {
            devices.forEach(device ->
                    lastDbStatus.put(device.getDeviceId(), device.getStatus() == 3)
            );
            FIRST_LOAD = false;
        }
    }

    public static void updateLastDbStatus(Map<Long, Integer> newStatus) {
        newStatus.forEach((deviceId, status) ->
                lastDbStatus.put(deviceId, status == 3)
        );
    }

}
