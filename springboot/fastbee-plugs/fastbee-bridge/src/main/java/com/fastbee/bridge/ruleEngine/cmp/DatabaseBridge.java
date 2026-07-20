package com.fastbee.bridge.ruleEngine.cmp;

import javax.sql.DataSource;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.DatabaseClientConfig;
import com.fastbee.bridge.service.IDatabaseClientServiceService;
import com.fastbee.rule.context.MsgContext;

@Slf4j
@LiteflowComponent("databaseBridge")
public class DatabaseBridge extends NodeComponent {
    @Autowired
    private IDatabaseClientServiceService dataSourceService;

    @Override
    public void process() throws Exception {
        MsgContext cxt = this.getContextBean(MsgContext.class);
        Integer id = cxt.getData("databaseBridgeID");
        Bridge bridge = dataSourceService.selectByBridgeId(id.longValue());
        if (bridge == null || bridge.getType() != 5) {
            log.error("数据源不存在或者当前配置类型不是数据库存储，id={}", id);
        }
        if (bridge != null && "1".equals(bridge.getEnable())) {
            DatabaseClientConfig config = (DatabaseClientConfig) dataSourceService.buildConfig(bridge);
            config.setPoolName(bridge.getName());
            DataSource dataSource = (DataSource) dataSourceService.instance(config);
            if (dataSource == null) {
                log.error("未找到对应的数据源配置，bridgeId: {}", id);
                throw new IllegalStateException("未找到对应的数据源配置");
            }
            config.setSql(cxt.placeholders(config.getSql()));
            dataSourceService.exec(config, dataSource);
        }
    }

}
