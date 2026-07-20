package com.fastbee.icc.util;

public class NumberUtil {

    /**
     * 判断字符串是否为整数
     *
     * @param str 需要判断的字符串
     * @return 如果字符串是整数则返回true，否则返回false
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
