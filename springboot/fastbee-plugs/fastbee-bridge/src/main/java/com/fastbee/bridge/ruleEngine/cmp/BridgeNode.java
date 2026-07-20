package com.fastbee.bridge.ruleEngine.cmp;

import java.util.Map;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.ruleEngine.data.BridgeData;
import com.fastbee.bridge.service.IBridge;
import com.fastbee.bridge.service.IDatabaseClientServiceService;
import com.fastbee.bridge.service.IHttpClientService;
import com.fastbee.bridge.service.IMqttClientService;
import com.fastbee.rule.context.BaseContext;

@Slf4j
@LiteflowComponent("bridge")
public class BridgeNode extends NodeComponent {
    @Resource
    private IHttpClientService httpClientService;

    @Resource
    private IMqttClientService mqttClientService;

    @Resource
    private IDatabaseClientServiceService dataSourceService;

    @Override
    public void process() throws Exception {
        BaseContext cxt = this.getContextBean(BaseContext.class);
        BridgeData data = this.getCmpData(BridgeData.class);
        Map<String, Object> dataMap = cxt.getDataMap();
        IBridge bridgeService = null;
        switch (data.getType()) {
            case 3:
                bridgeService = httpClientService;
                break;
            case 4:
                bridgeService = mqttClientService;
                break;
            case 5:
                bridgeService = dataSourceService;
                break;
            default:
                cxt.printDebugLog(this, "未定义的桥接类型");
        }

        if (bridgeService == null) {
            cxt.printDebugLog(this, "未能找到对应的桥接服务，type=%d", data.getType());
            return;
        }

        Config config = bridgeService.buildConfig(data);

        // 定义要覆盖的配置
        config.reload(this, cxt.getDataMap());
        cxt.printDebugLog(this, "重新加载的上下文传递配置：%s", cxt.getDataMap().toString());

        // 创建连接
        Object connect = bridgeService.instance(config);
        String payload = null;
        // 覆盖payload
        if (dataMap.containsKey("payload")) {
            payload = dataMap.get("payload").toString();
            // payload占位符替换
            payload = cxt.placeholders(payload);
            cxt.printDebugLog(this, "发送的 payload ：%s", payload);
        } else {
            cxt.printDebugLog(this, "dataMap 获取不到payload");
        }
        // 默认使用cxt的 payload传递桥接消息
        String ret = bridgeService.exec(config, connect, payload != null ? payload.getBytes() : null);
        cxt.setResult(ret);
        cxt.printDebugLog(this, "传递的 result ：%s", ret);
    }
}
