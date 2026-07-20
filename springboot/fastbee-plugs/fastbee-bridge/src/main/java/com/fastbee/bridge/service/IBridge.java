package com.fastbee.bridge.service;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.ruleEngine.data.BridgeData;

public interface IBridge {
    // 实例化一个连接
    Object instance(Config config);
    // 创建配置
    Config buildConfig(Bridge query);

    Config buildConfig(BridgeData query);
    // 执行连接
    String exec(Config config, Object connect);
    String exec(Config config, Object connect, byte[] msg);

    int connect(Object obj);
    int disconnect(Object obj);
}
