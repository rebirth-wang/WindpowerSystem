//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.mybatis.enums;

import com.fastbee.common.utils.StringUtils;

public enum DataBaseType {
    MY_SQL("MySQL"),
    ORACLE("Oracle"),
    POSTGRE_SQL("PostgreSQL"),
    SQL_SERVER("Microsoft SQL Server"),
    DM("DM DBMS"),
    KINGBASE("KingbaseES");

    private final String type;

    public static DataBaseType find(String databaseProductName) {
        if (StringUtils.isBlank(databaseProductName)) {
            return null;
        } else {
            for(DataBaseType var4 : values()) {
                if (var4.getType().equals(databaseProductName)) {
                    return var4;
                }
            }

            return null;
        }
    }

    public String getType() {
        return this.type;
    }

    private DataBaseType(String type) {
        this.type = type;
    }
}
