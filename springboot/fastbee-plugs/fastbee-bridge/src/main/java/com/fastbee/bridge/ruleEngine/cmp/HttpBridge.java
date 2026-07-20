package com.fastbee.bridge.ruleEngine.cmp;

import com.dtflys.forest.http.ForestRequest;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.model.HttpClientConfig;
import com.fastbee.bridge.service.IBridgeService;
import com.fastbee.bridge.service.IHttpClientService;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.rule.context.MsgContext;

@LiteflowComponent("httpBridge")
public class HttpBridge extends NodeComponent {

    @Autowired
    private IHttpClientService httpClientService;
    @Autowired
    private IBridgeService bridgeService;

    @Override
    public void process() throws Exception {
        // 获取上下文对象
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = null;
        Boolean httpPull = null;
        try {
            id = cxt.getData("httpBridgeID");
            httpPull = cxt.getData("httpPull");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bridge bridge = bridgeService.getById(Long.valueOf(id));
        if (bridge != null && "1".equals(bridge.getEnable())) {
            Config config = (HttpClientConfig) httpClientService.buildConfig(bridge);
            if (config == null ) {
                // 覆盖的配置
                config.reload(this, cxt.getDataMap());
                ForestRequest request = (ForestRequest) httpClientService.instance(config);
                HttpClientConfig http = (HttpClientConfig) config;
                // 默认使用桥接配置的 body  如果未配置则使用上下文中的 payload
                if (StringUtils.isNotBlank(http.getBody())) {
                    request.addBody(cxt.placeholders(http.getBody()));
                } else {
                    request.addBody("payload", "text/plian", cxt.getPayload());
                }
                // 传递桥接结果
                cxt.setPayload(httpClientService.exec(null, request));
            }
        }
    }
}
