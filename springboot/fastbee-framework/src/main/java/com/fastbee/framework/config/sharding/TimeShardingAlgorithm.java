//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config.sharding;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.fastbee.framework.config.sharding.enums.ShardingTableCacheEnum;

public class TimeShardingAlgorithm implements StandardShardingAlgorithm<Date> {
    private static final Logger log = LoggerFactory.getLogger(TimeShardingAlgorithm.class);
    private static final SimpleDateFormat TABLE_SHARD_Date_FORMATTER = new SimpleDateFormat("yyyyMM");
    private static final DateTimeFormatter TABLE_SHARD_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    private static final SimpleDateFormat DATE_TIME_FORMATTER_SPILE = new SimpleDateFormat("yyyy-MM-dd");
    private final String TABLE_SPLIT_SYMBOL = "_";

    public String doSharding(Collection<String> tableNames, PreciseShardingValue<Date> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
        this.createAllTable(logicTable, tableNames);
        log.info(">>>>>>>>>> 【INFO】精确分片，节点配置表名：{}，数据库缓存表名：{}", tableNames, logicTable.resultTableNamesCache());
        Date date = (Date)preciseShardingValue.getValue();
        Instant instant = date.toInstant();
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        String resultTableName = logicTableName + "_" + TABLE_SHARD_TIME_FORMATTER.format(localDateTime);
        if (!tableNames.contains(resultTableName)) {
            tableNames.add(resultTableName);
        }

        return ShardingAlgorithmTool.getShardingTableAndCreate(logicTable, resultTableName);
    }

    public Collection<String> doSharding(Collection<String> tableNames, RangeShardingValue<Date> rangeShardingValue) {
        log.info("开始分表查询开始:{}", System.currentTimeMillis());
        String logicTableName = rangeShardingValue.getLogicTableName();
        ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
        this.createAllTable(logicTable, tableNames);
        log.info(">>>>>>>>>> 【INFO】范围分片，节点配置表名：{}，数据库缓存表名：{}", tableNames, logicTable.resultTableNamesCache());
        Range<Date> valueRange = rangeShardingValue.getValueRange();
        boolean hasLowerBound = valueRange.hasLowerBound();
        boolean hasUpperBound = valueRange.hasUpperBound();
        Set<String> tableNameCache = logicTable.resultTableNamesCache();
        String min = hasLowerBound ? String.valueOf(valueRange.lowerEndpoint()) : this.getLowerEndpoint(tableNameCache);
        String max = hasUpperBound ? String.valueOf(valueRange.upperEndpoint()) : this.getUpperEndpoint(tableNameCache);
        Set<String> resultTableNames = new LinkedHashSet();

        try {
            Date minDate = DATE_TIME_FORMATTER_SPILE.parse(min);
            Date maxDate = DATE_TIME_FORMATTER_SPILE.parse(max);

            for(Calendar calendar = Calendar.getInstance(); minDate.before(maxDate) || minDate.equals(maxDate); minDate = calendar.getTime()) {
                String tableName = logicTableName + "_" + TABLE_SHARD_Date_FORMATTER.format(minDate);
                resultTableNames.add(tableName);
                calendar.setTime(minDate);
                calendar.add(5, 1);
            }

            log.info("开始分表查询结束:{}", System.currentTimeMillis());
            return ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, resultTableNames);
        } catch (Exception var16) {
            return ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, logicTable.resultTableNamesCache());
        }
    }

    public void init() {
    }

    public String getType() {
        return null;
    }

    private String getLowerEndpoint(Collection<String> tableNames) {
        Optional<LocalDateTime> optional = tableNames.stream().map((o) -> LocalDateTime.parse(o.replace("_", "") + "01 00:00:00", DATE_TIME_FORMATTER)).min(Comparator.comparing(Function.identity()));
        if (optional.isPresent()) {
            ZonedDateTime zonedDateTime = ((LocalDateTime)optional.get()).atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            return String.valueOf(Date.from(instant));
        } else {
            log.error(">>>>>>>>>> 【ERROR】获取数据最小分表失败，请稍后重试，tableName：{}", tableNames);
            throw new IllegalArgumentException("获取数据最小分表失败，请稍后重试");
        }
    }

    private String getUpperEndpoint(Collection<String> tableNames) {
        Optional<LocalDateTime> optional = tableNames.stream().map((o) -> LocalDateTime.parse(o.replace("_", "") + "01 00:00:00", DATE_TIME_FORMATTER)).max(Comparator.comparing(Function.identity()));
        if (optional.isPresent()) {
            ZonedDateTime zonedDateTime = ((LocalDateTime)optional.get()).atZone(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            return String.valueOf(Date.from(instant));
        } else {
            log.error(">>>>>>>>>> 【ERROR】获取数据最大分表失败，请稍后重试，tableName：{}", tableNames);
            throw new IllegalArgumentException("获取数据最大分表失败，请稍后重试");
        }
    }

    private void createAllTable(ShardingTableCacheEnum logicTable, Collection<String> tableNames) {
        if (CollectionUtils.isEmpty(logicTable.resultTableNamesCache())) {
            ShardingAlgorithmTool.getShardingTablesAndCreate(logicTable, tableNames);
            ShardingAlgorithmTool.tableNameCacheReload(logicTable);
        }
    }
}
