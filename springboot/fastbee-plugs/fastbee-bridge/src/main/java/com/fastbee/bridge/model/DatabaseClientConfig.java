package com.fastbee.bridge.model;

import java.util.Map;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSON;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import com.fastbee.common.annotation.Excel;

@Data
@NoArgsConstructor
public class DatabaseClientConfig implements Config {
    /**
     * 连接池名称
     */
    private String poolName;

    /**
     * JDBC driver org.h2.Driver
     */
    private String driverClassName;

    /**
     * 数据源
     */
    @Excel(name = "数据源")
    private String databaseSource;

    /**
     * 类型
     */
    @Excel(name = "类型")
    private String type;

    /**
     * 数据库名称
     */
    @Excel(name = "数据库名称")
    private String dataBaseName;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 连接地址
     */
    @Excel(name = "连接地址")
    private String host;

    /**
     * 用户名
     */
    @Excel(name = "用户名")
    private String username;

    /**
     * 密码
     */
    @Excel(name = "密码")
    private String password;

    /**
     * sql语句
     */
    @Excel(name = "sql语句")
    private String sql;

    @Override
    public void load(String configJson) {
        DatabaseClient config = JSON.parseObject(configJson, DatabaseClient.class);
        if (config.getPoolName() == null) {
            Snowflake snowflake = IdUtil.getSnowflake(1, 1);
            this.poolName = "Bridge_" + snowflake.nextId();
        } else {
            this.poolName = config.getPoolName();
        }
        this.driverClassName = config.getDriverClassName();
        this.databaseSource = config.getDatabaseSource();
        this.type = config.getType();
        this.dataBaseName = config.getDataBaseName();
        this.url = config.getUrl();
        this.host = config.getHost();
        this.username = config.getUsername();
        this.password = config.getPassword();
        this.sql = config.getSql();
    }

    @Override
    public void reload(NodeComponent node, Map<String, Object> dataMap) {
        StringSubstitutor substitutor = new StringSubstitutor(dataMap);

        if(dataMap.containsKey("sql")) {
            this.sql = dataMap.get("sql").toString();
        }
        this.sql = substitutor.replace(this.sql);
        if(dataMap.containsKey("dataBaseName")) {
            this.dataBaseName = dataMap.get("dataBaseName").toString();
        }
    }
}
