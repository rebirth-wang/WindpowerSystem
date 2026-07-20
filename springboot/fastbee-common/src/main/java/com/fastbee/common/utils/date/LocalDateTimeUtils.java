//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.date;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cn.hutool.core.date.LocalDateTimeUtil;

public class LocalDateTimeUtils {
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static LocalDateTime EMPTY = buildTime(1970, 1, 1);

    public static LocalDateTime addTime(Duration duration) {
        return LocalDateTime.now().plus(duration);
    }

    public static boolean beforeNow(LocalDateTime date) {
        return date.isBefore(LocalDateTime.now());
    }

    public static boolean afterNow(LocalDateTime date) {
        return date.isAfter(LocalDateTime.now());
    }

    public static LocalDateTime buildTime(int year, int mouth, int day) {
        return LocalDateTime.of(year, mouth, day, 0, 0, 0);
    }

    public static LocalDateTime[] buildBetweenTime(int year1, int mouth1, int day1, int year2, int mouth2, int day2) {
        return new LocalDateTime[]{buildTime(year1, mouth1, day1), buildTime(year2, mouth2, day2)};
    }

    public static boolean isBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null ? LocalDateTimeUtil.isIn(LocalDateTime.now(), startTime, endTime) : false;
    }

    public static String localDateTimeToStr(LocalDateTime localDateTime, String format) {
        DateTimeFormatter var2 = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(var2);
    }
}
