//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RPriorityBlockingQueue;
import org.redisson.api.RedissonClient;

import com.fastbee.common.utils.spring.SpringUtils;

public class QueueUtils {
    private static final RedissonClient CLIENT = (RedissonClient)SpringUtils.getBean(RedissonClient.class);

    public static RedissonClient getClient() {
        return CLIENT;
    }

    public static <T> boolean addQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.offer(data);
    }

    public static <T> T getQueueObject(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return (T)queue.poll();
    }

    public static <T> boolean removeQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.remove(data);
    }

    public static <T> boolean destroyQueue(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        return queue.delete();
    }

    public static <T> void addDelayedQueueObject(String queueName, T data, long time) {
        addDelayedQueueObject(queueName, data, time, TimeUnit.MILLISECONDS);
    }

    public static <T> void addDelayedQueueObject(String queueName, T data, long time, TimeUnit timeUnit) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        delayedQueue.offer(data, time, timeUnit);
    }

    public static <T> T getDelayedQueueObject(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        return (T)delayedQueue.poll();
    }

    public static <T> boolean removeDelayedQueueObject(String queueName, T data) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        return delayedQueue.remove(data);
    }

    public static <T> void destroyDelayedQueue(String queueName) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = CLIENT.getDelayedQueue(queue);
        delayedQueue.destroy();
    }

    public static <T> boolean addPriorityQueueObject(String queueName, T data) {
        RPriorityBlockingQueue<T> priorityBlockingQueue = CLIENT.getPriorityBlockingQueue(queueName);
        return priorityBlockingQueue.offer(data);
    }

    public static <T> T getPriorityQueueObject(String queueName) {
        RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
        return (T)queue.poll();
    }

    public static <T> boolean removePriorityQueueObject(String queueName, T data) {
        RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
        return queue.remove(data);
    }

    public static <T> boolean destroyPriorityQueue(String queueName) {
        RPriorityBlockingQueue<T> queue = CLIENT.getPriorityBlockingQueue(queueName);
        return queue.delete();
    }

    public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        return boundedBlockingQueue.trySetCapacity(capacity);
    }

    public static <T> boolean trySetBoundedQueueCapacity(String queueName, int capacity, boolean destroy) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        if (boundedBlockingQueue.isExists() && destroy) {
            destroyQueue(queueName);
        }

        return boundedBlockingQueue.trySetCapacity(capacity);
    }

    public static <T> boolean addBoundedQueueObject(String queueName, T data) {
        RBoundedBlockingQueue<T> boundedBlockingQueue = CLIENT.getBoundedBlockingQueue(queueName);
        return boundedBlockingQueue.offer(data);
    }

    public static <T> T getBoundedQueueObject(String queueName) {
        RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
        return (T)queue.poll();
    }

    public static <T> boolean removeBoundedQueueObject(String queueName, T data) {
        RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
        return queue.remove(data);
    }

    public static <T> boolean destroyBoundedQueue(String queueName) {
        RBoundedBlockingQueue<T> queue = CLIENT.getBoundedBlockingQueue(queueName);
        return queue.delete();
    }

    public static <T> void subscribeBlockingQueue(String queueName, Consumer<T> consumer, boolean isDelayed) {
        RBlockingQueue<T> queue = CLIENT.getBlockingQueue(queueName);
        if (isDelayed) {
            CLIENT.getDelayedQueue(queue);
        }

        queue.subscribeOnElements(consumer);
    }

    private QueueUtils() {
    }
}
