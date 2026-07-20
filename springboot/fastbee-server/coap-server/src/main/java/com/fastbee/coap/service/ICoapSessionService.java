package com.fastbee.coap.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fastbee.coap.model.CoapSession;

public interface ICoapSessionService {
    /**
     * session 会话存储
     *
     * @param clientId:    客户端标识
     * @param session: session会话

     */
    void storeSession(String clientId, CoapSession session);

    /**
     * 根据客户端标识获取相应会话
     *
     * @param clientId: 客户端标识
     */
    CoapSession getSession(String clientId);

    /**
     * 清除历史会话状态
     *
     * @param clientId: 客户端标识
     */
    void cleanSession(String clientId);

    /**
     * 根据客户端标识查看是否存在该会话
     *
     * @param clientId:
     */
    boolean containsKey(String clientId);

    /**
     * 获取集合
     * @return MAP
     */
    ConcurrentHashMap<String, CoapSession> getSessionMap();

    /**
     * map分页（从1开始）
     *
     * @param sourceMap 分页数据
     * @param pageSize     页面大小
     * @param currentPage  当前页面
     */
    public Map<String, CoapSession> listPage(Map<String, CoapSession> sourceMap, int pageSize, int currentPage);
}
