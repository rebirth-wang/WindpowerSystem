//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.mybatis.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import cn.hutool.core.convert.Convert;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.mybatis.enums.DataBaseType;
import com.fastbee.common.utils.spring.SpringUtils;

public class DataBaseHelper {
    private static final DynamicRoutingDataSource DS = (DynamicRoutingDataSource)SpringUtils.getBean(DynamicRoutingDataSource.class);
    public static final String DEFAULT_DATASOURCE_NAME = "master";

    public static DataBaseType getDataBaseType(String dataName) {
        DataSource dataSource = (DataSource)DS.getDataSources().get(dataName);

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            DataBaseType var6 = DataBaseType.find(databaseProductName);
            return var6;
        } catch (SQLException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    public static boolean isMySql() {
        return DataBaseType.MY_SQL == getDataBaseType("master");
    }

    public static boolean isOracle() {
        return DataBaseType.ORACLE == getDataBaseType("master");
    }

    public static boolean isPostgerSql() {
        return DataBaseType.POSTGRE_SQL == getDataBaseType("master");
    }

    public static boolean isSqlServer() {
        return DataBaseType.SQL_SERVER == getDataBaseType("master");
    }

    public static boolean isDm() {
        return DataBaseType.DM == getDataBaseType("master");
    }

    public static boolean isMySql(String dataName) {
        return DataBaseType.MY_SQL == getDataBaseType(dataName);
    }

    public static boolean isKingbase() {
        return DataBaseType.KINGBASE == getDataBaseType("master");
    }

    public static boolean isKingbase(String dataName) {
        return DataBaseType.KINGBASE == getDataBaseType(dataName);
    }

    public static boolean isOracle(String dataName) {
        return DataBaseType.ORACLE == getDataBaseType(dataName);
    }

    public static boolean isPostgerSql(String dataName) {
        return DataBaseType.POSTGRE_SQL == getDataBaseType(dataName);
    }

    public static boolean isSqlServer(String dataName) {
        return DataBaseType.SQL_SERVER == getDataBaseType(dataName);
    }

    public static boolean isDm(String dataName) {
        return DataBaseType.DM == getDataBaseType(dataName);
    }

    public static String findInSet(Object var1, String var2) {
        DataBaseType dataBasyType = getDataBaseType("master");
        String var = Convert.toStr(var1);
        if (dataBasyType == DataBaseType.SQL_SERVER) {
            return String.format("charindex(',%s,' , ','+%s+',') <> 0", var, var2);
        } else if (dataBasyType == DataBaseType.POSTGRE_SQL) {
            return String.format("(select strpos(','||%s||',' , ',%s,')) <> 0", var2, var);
        } else if (dataBasyType != DataBaseType.ORACLE && dataBasyType != DataBaseType.DM) {
            return dataBasyType == DataBaseType.KINGBASE ? String.format("instr(','||%s||',' , ',%s,') <> 0", var2, var) : String.format("find_in_set(%s , %s) <> 0", var, var2);
        } else {
            return String.format("instr(','||%s||',' , ',%s,') <> 0", var2, var);
        }
    }

    public static String findInSetColumn(String var1, String var2) {
        DataBaseType dataBasyType = getDataBaseType("master");
        String var = Convert.toStr(var1);
        if (dataBasyType == DataBaseType.SQL_SERVER) {
            return String.format("charindex(',' + CAST(%s AS VARCHAR(50)) + ',' , ',' + CAST('%s' AS VARCHAR(500)) + ',') <> 0", var, var2);
        } else if (dataBasyType == DataBaseType.POSTGRE_SQL) {
            return String.format("%s = ANY(string_to_array('%s', ',')::bigint[])", var, var2);
        } else if (dataBasyType != DataBaseType.ORACLE && dataBasyType != DataBaseType.DM) {
            return dataBasyType == DataBaseType.KINGBASE ? String.format("instr(CONCAT(CONCAT(',', %s), ','), CONCAT(CONCAT(',', %s), ',')) <> 0", var2, var) : String.format("find_in_set(%s , '%s') <> 0", var, var2);
        } else {
            return String.format("instr(','||%s||',' , ','||%s||',') <> 0", var2, var);
        }
    }

    public static List<String> getDataSourceNameList() {
        return new ArrayList(DS.getDataSources().keySet());
    }

    public static String getDeptCondition(Long deptId) {
        if (deptId != null && deptId != 0L) {
            if (isPostgerSql()) {
                return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE " + deptId + "::text = ANY(string_to_array(ancestors, ',')) OR dept_id = " + deptId + ")";
            } else if (isSqlServer()) {
                return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE CHARINDEX(',' + CAST(" + deptId + " AS VARCHAR) + ',', ',' + ancestors + ',') > 0 OR dept_id = " + deptId + ")";
            } else if (isOracle()) {
                return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")";
            } else if (isDm()) {
                return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(',' || ancestors || ',', ',' || " + deptId + " || ',') > 0 OR dept_id = " + deptId + ")";
            } else if (isMySql()) {
                return "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE FIND_IN_SET(" + deptId + ", ancestors) > 0 OR dept_id = " + deptId + ")";
            } else {
                return isKingbase() ? String.format("SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE INSTR(CONCAT(CONCAT(',', CAST(ancestors AS TEXT)), ','), CONCAT(CONCAT(',', CAST(%d AS TEXT)), ',')) > 0 OR dept_id = %d)", deptId, deptId) : "SELECT u.user_id FROM sys_user u WHERE u.dept_id IN (SELECT dept_id FROM sys_dept WHERE FIND_IN_SET(" + deptId + ", ancestors) > 0 OR dept_id = " + deptId + ")";
            }
        } else {
            return "1=1";
        }
    }

    public static String checkTime(Integer timeout) {
        if (timeout != null && timeout != 0) {
            if (isPostgerSql()) {
                return "CURRENT_TIMESTAMP > last_connect_time + interval '1 seconds' * " + timeout;
            } else if (isSqlServer()) {
                return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + " last_connect_time)";
            } else if (isOracle()) {
                return "CURRENT_TIMESTAMP > last_connect_time + (" + timeout + " / 86400)";
            } else if (isDm()) {
                return "CURRENT_TIMESTAMP > DATEADD(SECOND, " + timeout + ", last_connect_time)";
            } else if (isMySql()) {
                return "CURRENT_TIMESTAMP > DATE_ADD(last_connect_time, INTERVAL " + timeout + " SECOND)";
            } else {
                return isKingbase() ? "CURRENT_TIMESTAMP > DATE_ADD(last_connect_time, INTERVAL '" + timeout + "' SECOND)" : "CURRENT_TIMESTAMP > DATE_ADD(last_connect_time, INTERVAL " + timeout + " SECOND)";
            }
        } else {
            return "";
        }
    }

    private DataBaseHelper() {
    }
}
