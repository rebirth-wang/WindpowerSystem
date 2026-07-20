package com.fastbee.common.extend.core.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

/**
 * @author zzy
 * @description: 设备锁管理
 * @date 2025-10-31 17:39
 */
@Component
public class DeviceLockManager {

    private final ConcurrentHashMap<String, Lock> deviceLocks = new ConcurrentHashMap<>();

    /**
     * 获取设备的锁
     */
    public Lock getLockForDevice(String serialNumber) {
        return deviceLocks.computeIfAbsent(serialNumber, k -> new ReentrantLock());
    }

    /**
     * 清理不再使用的锁（防止内存泄漏）
     */
    public void removeLock(String serialNumber) {
        deviceLocks.remove(serialNumber);
    }

    /**
     * 执行带锁的操作
     */
    public <T> T executeWithLock(String serialNumber, Supplier<T> task) {
        Lock lock = getLockForDevice(serialNumber);
        lock.lock();
        try {
            return task.get();
        } finally {
            lock.unlock();
        }
    }

    public void executeWithLock(String serialNumber, Runnable task) {
        Lock lock = getLockForDevice(serialNumber);
        lock.lock();
        try {
            task.run();
        } finally {
            lock.unlock();
        }
    }
}
