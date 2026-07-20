package com.fastbee.iot.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import com.fastbee.framework.mybatis.helper.DataBaseHelper;

public class PostgresJsonStringTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 判断当前数据库类型
        if (DataBaseHelper.isKingbase() || DataBaseHelper.isPostgerSql()) {
            // PostgreSQL：使用 PGobject 包装 JSON
            PGobject pgObject = new PGobject();
            pgObject.setType("json");
            pgObject.setValue(parameter);
            ps.setObject(i, pgObject);
        } else {
            // MySQL：直接设置字符串
            ps.setString(i, parameter);
        }
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return "";
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return "";
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return "";
    }

}
