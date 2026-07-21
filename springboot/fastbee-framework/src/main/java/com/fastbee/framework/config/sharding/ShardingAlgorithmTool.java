//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.sharding;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.algorithm.config.AlgorithmProvidedShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.fastbee.framework.config.sharding.enums.ShardingTableCacheEnum;

public class ShardingAlgorithmTool {
    private static final Logger log = LoggerFactory.getLogger(ShardingAlgorithmTool.class);
    private static final String TABLE_SPLIT_SYMBOL = "_";
    private static final Environment ENV = SpringUtil.getApplicationContext().getEnvironment();
    private static final String DATASOURCE_URL;
    private static final String DATASOURCE_USERNAME;
    private static final String DATASOURCE_PASSWORD;

    public static Set<String> getShardingTablesAndCreate(ShardingTableCacheEnum logicTable, Collection<String> resultTableNames) {
        return (Set)resultTableNames.stream().map((o) -> getShardingTableAndCreate(logicTable, o)).collect(Collectors.toSet());
    }

    public static String getShardingTableAndCreate(ShardingTableCacheEnum logicTable, String resultTableName) {
        if (logicTable.resultTableNamesCache().contains(resultTableName)) {
            return resultTableName;
        } else {
            boolean isSuccess = createShardingTable(logicTable, resultTableName);
            return isSuccess ? resultTableName : logicTable.logicTableName();
        }
    }

    public static void tableNameCacheReloadAll() {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(ShardingAlgorithmTool::tableNameCacheReload);
    }

    public static void tableNameCacheReload(ShardingTableCacheEnum logicTable) {
        List<String> tableNameList = getAllTableNameBySchema(logicTable);
        logicTable.atomicUpdateCacheAndActualDataNodes(tableNameList);
        logicTable.resultTableNamesCache().clear();
        logicTable.resultTableNamesCache().addAll(tableNameList);
        actualDataNodesRefresh(logicTable.logicTableName(), tableNameList);
    }

    public static List<String> getAllTableNameBySchema(ShardingTableCacheEnum logicTable) {
        List<String> tableNames = new ArrayList();
        if (!StringUtils.isEmpty(DATASOURCE_URL) && !StringUtils.isEmpty(DATASOURCE_USERNAME) && !StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD)) {
                Statement st = conn.createStatement();
                Throwable var5 = null;

                try {
                    String logicTableName = logicTable.logicTableName();
                    ResultSet rs = st.executeQuery("show TABLES like '" + logicTableName + "_" + "%'");
                    Throwable var8 = null;

                    try {
                        log.info("查询数据库所有表:{}", "show TABLES like '" + logicTableName + "_" + "%'");

                        while(rs.next()) {
                            String tableName = rs.getString(1);
                            log.info("分表格式:{}", String.format("^(%s\\d{6})$", logicTableName + "_"));
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(tableName) && tableName.matches(String.format("^(%s\\d{6})$", logicTableName + "_"))) {
                                tableNames.add(rs.getString(1));
                            }
                        }
                    } catch (Throwable var55) {
                        var8 = var55;
                        throw var55;
                    } finally {
                        if (rs != null) {
                            if (var8 != null) {
                                try {
                                    rs.close();
                                } catch (Throwable var54) {
                                    var8.addSuppressed(var54);
                                }
                            } else {
                                rs.close();
                            }
                        }

                    }
                } catch (Throwable var57) {
                    var5 = var57;
                    throw var57;
                } finally {
                    if (st != null) {
                        if (var5 != null) {
                            try {
                                st.close();
                            } catch (Throwable var53) {
                                var5.addSuppressed(var53);
                            }
                        } else {
                            st.close();
                        }
                    }

                }

                return tableNames;
            } catch (SQLException e) {
                log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
                throw new IllegalArgumentException("数据库连接失败，请稍后重试");
            }
        } else {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", new Object[]{DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD});
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
    }

    public static void actualDataNodesRefresh(String logicTableName, List<String> tableNamesCache) {
        try {
            if (CollectionUtils.isEmpty(tableNamesCache)) {
                return;
            }

            String dbName = "ds0";
            log.info(">>>>>>>>>> 【INFO】更新分表配置，logicTableName:{}，tableNamesCache:{}", logicTableName, tableNamesCache);
            String newActualDataNodes = (String)tableNamesCache.stream().map((o) -> String.format("%s.%s", dbName, o)).collect(Collectors.joining(","));
            ShardingSphereDataSource shardingSphereDataSource = (ShardingSphereDataSource)SpringUtil.getBean(ShardingSphereDataSource.class);
            updateShardRuleActualDataNodes(shardingSphereDataSource, logicTableName, newActualDataNodes);
        } catch (Exception e) {
            log.error("初始化 动态表单失败，原因：{}", e.getMessage(), e);
        }

    }

    private static void updateShardRuleActualDataNodes(ShardingSphereDataSource dataSource, String logicTableName, String newActualDataNodes) {
        ContextManager contextManager = dataSource.getContextManager();
        String schemaName = "logic_db";
        Collection<RuleConfiguration> newRuleConfigList = new LinkedList();

        for(RuleConfiguration oldRuleConfig : dataSource.getContextManager().getMetaDataContexts().getMetaData(schemaName).getRuleMetaData().getConfigurations()) {
            if (oldRuleConfig instanceof AlgorithmProvidedShardingRuleConfiguration) {
                AlgorithmProvidedShardingRuleConfiguration oldAlgorithmConfig = (AlgorithmProvidedShardingRuleConfiguration)oldRuleConfig;
                AlgorithmProvidedShardingRuleConfiguration newAlgorithmConfig = new AlgorithmProvidedShardingRuleConfiguration();
                Collection<ShardingTableRuleConfiguration> newTableRuleConfigList = new LinkedList();
                Collection<ShardingTableRuleConfiguration> oldTableRuleConfigList = oldAlgorithmConfig.getTables();
                oldTableRuleConfigList.forEach((oldTableRuleConfig) -> {
                    if (logicTableName.equals(oldTableRuleConfig.getLogicTable())) {
                        ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(oldTableRuleConfig.getLogicTable(), newActualDataNodes);
                        newTableRuleConfig.setTableShardingStrategy(oldTableRuleConfig.getTableShardingStrategy());
                        newTableRuleConfig.setDatabaseShardingStrategy(oldTableRuleConfig.getDatabaseShardingStrategy());
                        newTableRuleConfig.setKeyGenerateStrategy(oldTableRuleConfig.getKeyGenerateStrategy());
                        newTableRuleConfigList.add(newTableRuleConfig);
                    } else {
                        newTableRuleConfigList.add(oldTableRuleConfig);
                    }

                });
                newAlgorithmConfig.setTables(newTableRuleConfigList);
                newAlgorithmConfig.setAutoTables(oldAlgorithmConfig.getAutoTables());
                newAlgorithmConfig.setBindingTableGroups(oldAlgorithmConfig.getBindingTableGroups());
                newAlgorithmConfig.setBroadcastTables(oldAlgorithmConfig.getBroadcastTables());
                newAlgorithmConfig.setDefaultDatabaseShardingStrategy(oldAlgorithmConfig.getDefaultDatabaseShardingStrategy());
                newAlgorithmConfig.setDefaultTableShardingStrategy(oldAlgorithmConfig.getDefaultTableShardingStrategy());
                newAlgorithmConfig.setDefaultKeyGenerateStrategy(oldAlgorithmConfig.getDefaultKeyGenerateStrategy());
                newAlgorithmConfig.setDefaultShardingColumn(oldAlgorithmConfig.getDefaultShardingColumn());
                newAlgorithmConfig.setShardingAlgorithms(oldAlgorithmConfig.getShardingAlgorithms());
                newAlgorithmConfig.setKeyGenerators(oldAlgorithmConfig.getKeyGenerators());
                newRuleConfigList.add(newAlgorithmConfig);
            }
        }

        contextManager.alterRuleConfiguration(schemaName, newRuleConfigList);
    }

    private static boolean createShardingTable(ShardingTableCacheEnum logicTable, String resultTableName) {
        String month = resultTableName.replace(logicTable.logicTableName() + "_", "");
        YearMonth shardingMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        if (shardingMonth.isAfter(YearMonth.now())) {
            return false;
        } else {
            synchronized(logicTable.logicTableName().intern()) {
                if (logicTable.resultTableNamesCache().contains(resultTableName)) {
                    return false;
                } else {
                    executeSql(Collections.singletonList("CREATE TABLE IF NOT EXISTS `" + resultTableName + "` LIKE `" + logicTable.logicTableName() + "`;"));
                    tableNameCacheReload(logicTable);
                    return true;
                }
            }
        }
    }

    private static void executeSql(List<String> sqlList) {
        if (!StringUtils.isEmpty(DATASOURCE_URL) && !StringUtils.isEmpty(DATASOURCE_USERNAME) && !StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD)) {
                try {
                    Statement st = conn.createStatement();
                    Throwable var4 = null;

                    try {
                        conn.setAutoCommit(false);

                        for(String sql : sqlList) {
                            st.execute(sql);
                        }
                    } catch (Throwable var32) {
                        var4 = var32;
                        throw var32;
                    } finally {
                        if (st != null) {
                            if (var4 != null) {
                                try {
                                    st.close();
                                } catch (Throwable var31) {
                                    var4.addSuppressed(var31);
                                }
                            } else {
                                st.close();
                            }
                        }

                    }
                } catch (Exception e) {
                    conn.rollback();
                    log.error(">>>>>>>>>> 【ERROR】数据表创建执行失败，请稍后重试，原因：{}", e.getMessage(), e);
                    throw new IllegalArgumentException("数据表创建执行失败，请稍后重试");
                }

            } catch (SQLException e) {
                log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
                throw new IllegalArgumentException("数据库连接失败，请稍后重试");
            }
        } else {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", new Object[]{DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD});
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
    }

    static {
        DATASOURCE_URL = ENV.getProperty("spring.shardingsphere.datasource.ds0.url");
        DATASOURCE_USERNAME = ENV.getProperty("spring.shardingsphere.datasource.ds0.username");
        DATASOURCE_PASSWORD = ENV.getProperty("spring.shardingsphere.datasource.ds0.password");
    }
}
