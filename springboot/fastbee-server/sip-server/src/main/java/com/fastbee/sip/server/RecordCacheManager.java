package com.fastbee.sip.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import com.fastbee.media.model.RecordList;

@Component
public class RecordCacheManager {
    private final ConcurrentHashMap<String, RecordList> recordMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public  void put(String key,RecordList list){
        recordMap.put(key, list);
    }

    public  RecordList get(String key){
        return recordMap.computeIfAbsent(key, k -> new RecordList());
    }

    public void remove(String key) {
        recordMap.remove(key);
        lockMap.remove(key);
    }

    public void addlock(String key){
        lockMap.computeIfAbsent(key, k -> new ReentrantLock());
    }

    public ReentrantLock getlock(String key){
        return lockMap.get(key);
    }


}
