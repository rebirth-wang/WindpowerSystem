package com.fastbee.bridge.service;

import java.util.List;

import javax.sql.DataSource;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.DatabaseClientConfig;

public interface IDatabaseClientServiceService extends IBridge {

    List<DatabaseClientConfig> selectList(DatabaseClientConfig ds);

    DatabaseClientConfig selectByDataName(String dataName);

    Bridge selectByBridgeId(Long id);

    DataSource selectDataSource(DatabaseClientConfig config);

    int insert(DatabaseClientConfig ds);

    void deletesByDataName(String dataName);
    
    /**
     * 根据数据源名称断开连接
     * @param dataSourceName 数据源名称
     * @return 1表示成功断连，0表示失败
     */
    int disconnectByName(String dataSourceName);
}
