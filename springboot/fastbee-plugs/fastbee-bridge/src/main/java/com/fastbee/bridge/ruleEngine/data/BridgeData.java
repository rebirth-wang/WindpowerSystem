package com.fastbee.bridge.ruleEngine.data;

import lombok.Data;

import com.fastbee.rule.cmp.data.BaseData;

@Data
public class BridgeData extends BaseData {
    private Long id;

    private String name;

    private String enable;

    private Integer status;

    // 3:mqtt 4:http 5:数据库 6:mq消息组件 5:其他
    private Integer type;

    private String configJson;

    private Long direction;

    private String route;

    private String response;
}
