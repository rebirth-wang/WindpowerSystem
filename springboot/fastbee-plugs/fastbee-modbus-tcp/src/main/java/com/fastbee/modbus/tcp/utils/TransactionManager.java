package com.fastbee.modbus.tcp.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.stereotype.Component;

import com.fastbee.modbus.tcp.model.RequestContext;

/**
 * 事务id管理
 */
@Component("transactionM")
public class TransactionManager {
    private final ConcurrentMap<String, RequestContext> contexts = new ConcurrentHashMap<>(65536);

    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();

    private final ConcurrentMap<Long, LongAdder> deviceTransactions = new ConcurrentHashMap<>();


    public TransactionManager() {
        cleaner.scheduleAtFixedRate(() ->
                contexts.entrySet().removeIf(e ->
                        System.currentTimeMillis() - e.getValue().getTimestamp() > 180_000
                ), 5, 5, TimeUnit.MINUTES
        );
    }

    public int nextTransactionId(Long deviceId) {
        LongAdder counter = deviceTransactions.computeIfAbsent(deviceId, k -> new LongAdder());
        counter.increment();
        return (int) (counter.sum() & 0xFFFF);
    }


    public void putContext(int transactionId, RequestContext ctx) {
        ctx.setTimestamp(System.currentTimeMillis());
        String key = getKey(transactionId, ctx.getDeviceId());
        contexts.put(key, ctx);
    }

    public RequestContext getContext(int transactionId, long deviceId) {
        String key = getKey(transactionId, deviceId);
        return contexts.get(key);
    }

    public void removeContext(int transactionId, long deviceId) {
        String key = getKey(transactionId, deviceId);
        contexts.remove(key);
    }

    private static  String getKey(int transactionId, long deviceId){
        return transactionId + "_" + deviceId;
    }
}
