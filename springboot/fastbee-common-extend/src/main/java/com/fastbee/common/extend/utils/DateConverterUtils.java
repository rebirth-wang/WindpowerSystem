package com.fastbee.common.extend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateConverterUtils {

    /**
     * 将Date转换为LocalDateTime（使用系统默认时区）
     */
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 将Date转换为LocalDateTime（指定时区）
     */
    public static LocalDateTime convertToLocalDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(zoneId)
                .toLocalDateTime();
    }

    /**
     * 将Date转换为LocalDate（只保留日期部分）
     */
    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * 将Date转换为LocalTime（只保留时间部分）
     */
    public static LocalTime convertToLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    /**
     * 将Date转换为指定格式的字符串
     */
    public static String convertToString(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = convertToLocalDateTime(date);
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将LocalDateTime转换为Date
     */
    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将LocalDate转换为Date（时间部分设为00:00:00）
     */
    public static Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 示例：处理不同时区
     */
    public static void handleDifferentTimeZones() {
        Date date = new Date();

        // 转换为不同时区的LocalDateTime
        LocalDateTime utcDateTime = date.toInstant()
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime();
        System.out.println("UTC时间: " + utcDateTime);

        LocalDateTime shanghaiDateTime = date.toInstant()
                .atZone(ZoneId.of("Asia/Shanghai"))
                .toLocalDateTime();
        System.out.println("上海时间: " + shanghaiDateTime);

        LocalDateTime newYorkDateTime = date.toInstant()
                .atZone(ZoneId.of("America/New_York"))
                .toLocalDateTime();
        System.out.println("纽约时间: " + newYorkDateTime);
    }

    public static void main(String[] args) {
        // 示例使用
        Date currentDate = new Date();

        // 转换为LocalDateTime
        LocalDateTime localDateTime = convertToLocalDateTime(currentDate);
        System.out.println("LocalDateTime: " + localDateTime);

        // 转换为LocalDate
        LocalDate localDate = convertToLocalDate(currentDate);
        System.out.println("LocalDate: " + localDate);

        // 转换为LocalTime
        LocalTime localTime = convertToLocalTime(currentDate);
        System.out.println("LocalTime: " + localTime);

        // 格式化为字符串
        String formatted = convertToString(currentDate, "yyyy-MM-dd HH:mm:ss");
        System.out.println("格式化: " + formatted);

        // 处理不同时区
        handleDifferentTimeZones();

        // 转换回Date
        Date convertedBack = convertToDate(localDateTime);
        System.out.println("转换回Date: " + convertedBack);
    }
}