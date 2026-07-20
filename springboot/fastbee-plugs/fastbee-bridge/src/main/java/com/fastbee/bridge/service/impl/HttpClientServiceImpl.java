package com.fastbee.bridge.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestRequest;
import org.springframework.stereotype.Service;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.model.HttpClientConfig;
import com.fastbee.bridge.ruleEngine.data.BridgeData;
import com.fastbee.bridge.service.IHttpClientService;
import com.fastbee.http.service.SuccessCondition;

/**
 * http桥接配置Service业务层处理
 *
 * @author gx_magx_ma
 * @date 2024-06-03
 */
@Service
public class HttpClientServiceImpl implements IHttpClientService {

    @Override
    public ForestRequest instance(Config config) {
        HttpClientConfig conf = (HttpClientConfig) config;
        ForestRequest request = Forest.request();
        if (conf.getParams() != null) {
            request.addQuery(conf.getParams());
        }
        if (conf.getHeaders() != null) {
            request.addHeader(conf.getHeaders());
        }
        if (conf.getBody() != null) {
            request.addBody(conf.getBody());
        }
        return request.url(conf.getUrl())
                .type(conf.getMethod())
                .backend(conf.getBackend());
    }

    @Override
    public Config buildConfig(Bridge bridge) {
        HttpClientConfig config = new HttpClientConfig();
        config.load(bridge.getConfigJson());
        return config;
    }

    @Override
    public Config buildConfig(BridgeData data) {
        HttpClientConfig config = new HttpClientConfig();
        config.load(data.getConfigJson());
        return config;
    }

    @Override
    public String exec(Config config, Object connect) {
        ForestRequest request = (ForestRequest) connect;
        AtomicReference<String> ret = new AtomicReference<>("");
        request.onSuccess(((data, req, res) -> {
            ret.set(res.getContent());
        })).execute();
        return ret.get();
    }

    @Override
    public String exec(Config config, Object connect, byte[] msg) {
        ForestRequest request = (ForestRequest) connect;
        AtomicReference<String> ret = new AtomicReference<>("");
        if (msg != null) {
            String strMsg = new String(msg, StandardCharsets.UTF_8);
            request.addBody(strMsg);
        }
        request.onSuccess(((data, req, res) -> {
            ret.set(res.getContent());
        })).execute();
        return ret.get();
    }

    @Override
    public int connect(Object obj) {
        AtomicInteger ret = new AtomicInteger();
        ForestRequest request = (ForestRequest) obj;
        request.successWhen(SuccessCondition.class)
                .onSuccess(((data, req, res) -> {
                    ret.set(1);
                }))
                .onError((ex, req, res) -> {
                    ret.set(0);
                }).execute();
        return ret.get();
    }

    @Override
    public int disconnect(Object obj) {
        return 0;
    }
}
