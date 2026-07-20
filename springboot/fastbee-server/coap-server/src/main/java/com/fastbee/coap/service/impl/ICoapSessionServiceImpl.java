package com.fastbee.coap.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.coap.model.CoapSession;
import com.fastbee.coap.service.ICoapSessionService;

@Slf4j
@Service
public class ICoapSessionServiceImpl implements ICoapSessionService {
    private final ConcurrentHashMap<String, CoapSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void storeSession(String clientId, CoapSession session) {
        sessionMap.put(clientId, session);
    }

    @Override
    public CoapSession getSession(String clientId) {
        return sessionMap.get(clientId);
    }

    @Override
    public void cleanSession(String clientId) {
        sessionMap.remove(clientId);
    }

    @Override
    public boolean containsKey(String clientId) {
        return sessionMap.containsKey(clientId);
    }

    @Override
    public ConcurrentHashMap<String, CoapSession> getSessionMap() {
        return sessionMap;
    }

    @Override
    public Map<String, CoapSession> listPage(Map<String, CoapSession> sourceMap, int pageSize, int currentPage) {
        Map<String, CoapSession> map = new LinkedHashMap<>();
        if (!sourceMap.isEmpty()) {
            AtomicInteger flag = new AtomicInteger(0);
            AtomicInteger size = new AtomicInteger(0);
            int currIdx = (currentPage > 1  ? (currentPage - 1) * pageSize : 0);
            sourceMap.forEach((ass, list_km) -> {
                if (flag.get() >= currIdx) {
                    if (size.get() < pageSize) {
                        map.put(ass, list_km);
                    } else {
                        return;
                    }
                    size.getAndIncrement();
                }
                flag.getAndIncrement();
            });

        }
        return map;
    }
}
