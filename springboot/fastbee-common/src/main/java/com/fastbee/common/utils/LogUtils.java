package com.fastbee.common.utils;

/**
 * @author fastbee
 * @date 2025-09-08 20:53
 * @description:
 */

public class LogUtils {
    public LogUtils() {
    }

    public static String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }

        return "[" + msg.toString() + "]";
    }
}
