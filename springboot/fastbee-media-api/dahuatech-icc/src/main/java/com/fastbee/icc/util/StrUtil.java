package com.fastbee.icc.util;

public class StrUtil {

    /**
     * 判断字符串是否不为空白
     *
     * @param str 需要判断的字符串
     * @return 如果字符串不为空白则返回true，否则返回false
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断的字符串
     * @return 如果字符串为空则返回true，否则返回false
     */
    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }
}
