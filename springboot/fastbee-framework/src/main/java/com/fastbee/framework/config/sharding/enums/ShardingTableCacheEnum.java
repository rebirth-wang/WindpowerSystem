//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.sharding.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import com.fastbee.framework.config.sharding.ShardingAlgorithmTool;

public enum ShardingTableCacheEnum {
    DEVICE_LOG("iot_device_log", new HashSet());

    private final String logicTableName;
    private final Set<String> resultTableNamesCache;
    private static Map<String, ShardingTableCacheEnum> valueMap = new HashMap();

    private ShardingTableCacheEnum(String logicTableName, Set<String> resultTableNamesCache) {
        this.logicTableName = logicTableName;
        this.resultTableNamesCache = resultTableNamesCache;
    }

    public static ShardingTableCacheEnum of(String value) {
        return (ShardingTableCacheEnum)valueMap.get(value);
    }

    public String logicTableName() {
        return this.logicTableName;
    }

    public Set<String> resultTableNamesCache() {
        return this.resultTableNamesCache;
    }

    public void atomicUpdateCacheAndActualDataNodes(List<String> tableNameList) {
        if (!CollectionUtils.isEmpty(tableNameList)) {
            synchronized(this.resultTableNamesCache) {
                this.resultTableNamesCache.clear();
                this.resultTableNamesCache.addAll(tableNameList);
                ShardingAlgorithmTool.actualDataNodesRefresh(this.logicTableName, tableNameList);
            }
        }
    }

    public static Set<String> logicTableNames() {
        return valueMap.keySet();
    }

    public String toString() {
        return "ShardingTableCacheEnum{logicTableName='" + this.logicTableName + '\'' + ", resultTableNamesCache=" + this.resultTableNamesCache + '}';
    }

    static {
        Arrays.stream(values()).forEach((o) -> {
            ShardingTableCacheEnum var10000 = (ShardingTableCacheEnum)valueMap.put(o.logicTableName, o);
        });
    }
}
