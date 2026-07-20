package com.fastbee.bridge.ruleEngine.cmp;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.mapper.BridgeMapper;
import com.fastbee.bridge.model.MqttClientConfig;
import com.fastbee.bridge.service.IMqttClientService;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.rule.context.MsgContext;

/**
 * @author zhuangpengli
 */
@Slf4j
@LiteflowComponent("mqttBridge")
public class MqttBridge extends NodeComponent {
    @Resource
    private BridgeMapper bridgeMapper;

    @Autowired
    private IMqttClientService mqttClientService;

    @Override
    public void process() throws Exception {
        // 获取上下文对象
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = cxt.getData("mqttBridgeID");
        Bridge bridge = bridgeMapper.selectById(Long.valueOf(id));
        if (bridge != null && "1".equals(bridge.getEnable())) {
            MqttClientConfig config = (MqttClientConfig) mqttClientService.buildConfig(bridge);
            // 默认使用桥接配置的 route  如果未配置则使用上下文中的 topic
            if (StringUtils.isEmpty(bridge.getRoute())) {
                config.setTopic(cxt.getTopic());
            }
            // 覆盖的配置
            config.reload(this, cxt.getDataMap());
            MqttAsyncClient client = (MqttAsyncClient) mqttClientService.instance(config);
            try {
                mqttClientService.exec(config, client, cxt.getPayload().getBytes());
                log.info("=>桥接发布主题成功 topic={},message={}", cxt.getTopic(), cxt.getPayload());
            } catch (RuntimeException e) {
                log.error("msg:"+e.getMessage());
                log.error("cause:"+e.getCause());
                log.error("=>桥接发布主题时发生错误 topic={},message={}", cxt.getTopic(), e.getMessage());
            }
        }
    }
}
