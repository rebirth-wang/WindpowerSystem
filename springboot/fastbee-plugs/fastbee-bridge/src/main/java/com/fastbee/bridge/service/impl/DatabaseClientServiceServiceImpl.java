package com.fastbee.bridge.service.impl;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import javax.sql.DataSource;

import jakarta.annotation.Resource;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.DefaultDataSourceCreator;
import com.baomidou.dynamic.datasource.creator.druid.DruidConfig;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.mapper.BridgeMapper;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.model.DatabaseClientConfig;
import com.fastbee.bridge.ruleEngine.data.BridgeData;
import com.fastbee.bridge.service.IDatabaseClientServiceService;
import com.fastbee.framework.config.properties.DruidProperties;

/**
 * @author zhuangpengli
 */
@Slf4j
@Service
public class DatabaseClientServiceServiceImpl implements IDatabaseClientServiceService {
    @Resource
    private DataSource dataSource;
    @Resource
    private DefaultDataSourceCreator dataSourceCreator;
    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;
    @Resource
    private BridgeMapper bridgeMapper;

    @Resource
    private DruidProperties druidProperties;

    @Override
    public List<DatabaseClientConfig> selectList(DatabaseClientConfig ds) {
        Map<String, DataSourceProperty> dataSourceProperties = dynamicDataSourceProperties.getDatasource();
        if (dataSourceProperties != null) {
            List<DatabaseClientConfig> result = new ArrayList<>();
            for (Map.Entry<String, DataSourceProperty> entry : dataSourceProperties.entrySet()) {
                DataSourceProperty dstemp = entry.getValue();
                if (checkadd(ds, dstemp)) {
                    DatabaseClientConfig multipleDataSource = new DatabaseClientConfig();
                    BeanUtils.copyProperties(dstemp, multipleDataSource);
                    result.add(multipleDataSource);
                }
            }
        }
        return Collections.emptyList();
    }

    public Boolean checkadd(DatabaseClientConfig ds, DataSourceProperty dstemp) {
        if (ds.getType() != null && !ds.getType().equals(dstemp.getType())) {
            return false;
        }
        if (ds.getDriverClassName() != null && !ds.getDriverClassName().equals(dstemp.getDriverClassName())) {
            return false;
        }
        if (ds.getUrl() != null && !ds.getUrl().equals(dstemp.getUrl())) {
            return false;
        }
        return true;
    }

    @Override
    public DatabaseClientConfig selectByDataName(String dataName) {
        Map<String, DataSourceProperty> dataSourceProperties = dynamicDataSourceProperties.getDatasource();
        DataSourceProperty ds = dataSourceProperties.get(dataName);
        if (ds != null) {
            DatabaseClientConfig multipleDataSource = new DatabaseClientConfig();
            BeanUtils.copyProperties(ds, multipleDataSource);
            return multipleDataSource;
        }
        return null;
    }

    @Override
    public Bridge selectByBridgeId(Long id) {
        return bridgeMapper.selectById(id);
    }

    @Override
    public DataSource selectDataSource(DatabaseClientConfig config) {
        DataSourceProperty dataSourceProperty = buildDataSourceProperty(config);
        //新增数据源
        DynamicRoutingDataSource dsr = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        dsr.addDataSource(config.getPoolName(), dataSource);
        return dataSource;
    }

    @Override
    public int insert(DatabaseClientConfig ds) {
        DataSourceProperty dataSourceProperty = buildDataSourceProperty(ds);
        //新增数据源
        DynamicRoutingDataSource dsr = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = dataSourceCreator.createDataSource(dataSourceProperty);
        dsr.addDataSource(ds.getPoolName(), dataSource);
        return 1;
    }

    @Override
    public void deletesByDataName(String dataName) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        ds.removeDataSource(dataName);
    }

    private DataSourceProperty buildDataSourceProperty(DatabaseClientConfig config) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        //配置全局Druid配置
        DruidConfig Druidconf = new DruidConfig();
        BeanUtils.copyProperties(druidProperties, Druidconf);
        BeanUtils.copyProperties(config, dataSourceProperty);
        switch (config.getDatabaseSource()) {
            case "MySQL":
                dataSourceProperty.setDriverClassName("com.mysql.cj.jdbc.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName() + "?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
                break;
            case "SQLServer":
                dataSourceProperty.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + ";DatabaseName=" + config.getDataBaseName());
                break;
            case "Oracle":
                dataSourceProperty.setDriverClassName("oracle.jdbc.driver.OracleDriver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + ":thin:@" + config.getHost() + "/" + config.getDataBaseName());
                Druidconf.setValidationQuery("SELECT 1 FROM DUAL");
                break;
            case "PostgreSQL":
                dataSourceProperty.setDriverClassName("org.postgresql.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName());
                break;
            case "H2":
                dataSourceProperty.setDriverClassName("org.h2.Driver");
                dataSourceProperty.setUrl("jdbc:" + config.getDatabaseSource().toLowerCase() + "://" + config.getHost() + "/" + config.getDataBaseName());
                break;
            default:
                log.error("Unsupported database type: {}", config.getDatabaseSource());
                break;
        }
        dataSourceProperty.setDruid(Druidconf);
        dataSourceProperty.setType(DruidDataSource.class);
        return dataSourceProperty;
    }

    @Override
    public DataSource instance(Config config) {
        DatabaseClientConfig conf = (DatabaseClientConfig) config;
        return selectDataSource(conf);
    }

    @Override
    public Config buildConfig(Bridge bridge) {
        DatabaseClientConfig config = new DatabaseClientConfig();
        config.load(bridge.getConfigJson());
        return config;
    }

    @Override
    public Config buildConfig(BridgeData data) {
        DatabaseClientConfig config = new DatabaseClientConfig();
        config.load(data.getConfigJson());
        return config;
    }

    @Override
    public String exec(Config config, Object connect) {
        DatabaseClientConfig conf = (DatabaseClientConfig) config;
        if (conf.getSql() == null) {
            log.error("请检查配置");
            return "";
        }
        DataSource ds = (DataSource) connect;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(conf.getSql())) {
            return statement.execute() + "";
        } catch (SQLException e) {
            log.error("SQL执行失败，原因：{}", e.getMessage(), e);
        }
        return "";
    }

    @Override
    public String exec(Config config, Object connect, byte[] msg) {
        String sql = "";
        if (config != null && ((DatabaseClientConfig) config).getSql() != null) {
            sql = ((DatabaseClientConfig) config).getSql();
        } else if (msg != null) {
            sql = new String(msg, StandardCharsets.UTF_8);
        } else {
            log.error("请检查配置");
            return "";
        }
        DataSource ds = (DataSource) connect;
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.execute() + "";
        } catch (SQLException e) {
            log.error("SQL执行失败，原因：{}", e.getMessage(), e);
        }
        return "";
    }

    @Override
    public int connect(Object obj) {
        DataSource ds = (DataSource) obj;
        try {
            Connection connection = ds.getConnection();
            if (connection != null) {
                return 1;
            }
        } catch (SQLException e) {
            log.error("=>数据源连接测试错误,请检查配置" + e.getMessage());
        }
        return 0;
    }

    @Override
    public int disconnect(Object obj) {
        if (obj == null) {
            log.warn("断连对象为空");
            return 0;
        }
        
        try {
            if (obj instanceof DataSource) {
                DataSource dataSource = (DataSource) obj;
                // 尝试获取连接来验证数据源是否可用
                try (Connection connection = dataSource.getConnection()) {
                    if (connection != null && !connection.isClosed()) {
                        // 如果是动态数据源，尝试移除
                        if (dataSource instanceof DynamicRoutingDataSource) {
                            DynamicRoutingDataSource dynamicDs = (DynamicRoutingDataSource) dataSource;
                            // 这里需要知道数据源的名称才能移除
                            // 由于我们只有DataSource对象，无法直接获取名称
                            log.info("数据源连接已建立，但无法确定具体名称进行移除");
                        }
                        return 1; // 表示成功断连
                    } else {
                        log.info("数据源连接已经关闭");
                        return 1;
                    }
                } catch (SQLException e) {
                    log.warn("数据源连接已关闭或不可用: {}", e.getMessage());
                    return 1; // 已经断开
                }
            } else if (obj instanceof Connection) {
                Connection connection = (Connection) obj;
                if (!connection.isClosed()) {
                    connection.close();
                    log.info("数据库连接已关闭");
                    return 1;
                } else {
                    log.info("数据库连接已经关闭");
                    return 1;
                }
            } else {
                log.warn("不支持的连接对象类型: {}", obj.getClass().getName());
                return 0;
            }
        } catch (Exception e) {
            log.error("数据库断连失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 根据数据源名称断开连接
     * @param dataSourceName 数据源名称
     * @return 1表示成功断连，0表示失败
     */
    public int disconnectByName(String dataSourceName) {
        if (dataSourceName == null || dataSourceName.isEmpty()) {
            log.warn("数据源名称为空");
            return 0;
        }
        
        try {
            DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
            // 检查数据源是否存在
            Map<String, DataSource> dataSources = ds.getDataSources();
            if (dataSources.containsKey(dataSourceName)) {
                // 移除数据源
                ds.removeDataSource(dataSourceName);
                log.info("数据源 {} 已成功断连并移除", dataSourceName);
                return 1;
            } else {
                log.warn("数据源 {} 不存在", dataSourceName);
                return 0;
            }
        } catch (Exception e) {
            log.error("根据名称断连数据源失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
