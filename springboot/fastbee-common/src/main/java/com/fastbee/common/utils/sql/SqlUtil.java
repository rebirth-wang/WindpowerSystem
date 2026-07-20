//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.sql;

import com.fastbee.common.exception.UtilException;
import com.fastbee.common.utils.StringUtils;

public class SqlUtil {
    public static String SQL_REGEX = "and |extractvalue|updatexml|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |+|user()";
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        } else {
            return value;
        }
    }

    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }

    public static void filterKeyword(String value) {
        if (!StringUtils.isEmpty(value)) {
            String[] var1 = StringUtils.split(SQL_REGEX, "\\|");

            for(String var5 : var1) {
                if (StringUtils.indexOfIgnoreCase(value, var5) > -1) {
                    throw new UtilException("参数存在SQL注入风险");
                }
            }

        }
    }
}
