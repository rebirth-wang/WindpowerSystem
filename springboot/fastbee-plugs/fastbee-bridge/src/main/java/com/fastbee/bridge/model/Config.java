package com.fastbee.bridge.model;

import java.util.Map;

import com.yomahub.liteflow.core.NodeComponent;

public interface Config {
    void load(String configJson);
    // 配置覆盖
    void reload(NodeComponent node, Map<String, Object> dataMap);
}
