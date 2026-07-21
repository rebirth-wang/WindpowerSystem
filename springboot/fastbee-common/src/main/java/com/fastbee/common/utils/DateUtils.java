//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";
    public static String YYYY_MM = "yyyy-MM";
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String SS_MM_HH_DD_HH_YY = "ssmmHHddMMyy";
    public static String YY_MM_DD_HH_MM_SS = "yy-MM-dd HH:mm:ss";
    private static String[] as = new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};
    public static String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static Date getNowDate() {
        return new Date();
    }

    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(String format, Date date) {
        return (new SimpleDateFormat(format)).format(date);
    }

    public static final Date dateTime(String format, String ts) {
        try {
            return (new SimpleDateFormat(format)).parse(ts);
        } catch (ParseException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static final String datePath() {
        Date var0 = new Date();
        return DateFormatUtils.format(var0, "yyyy/MM/dd");
    }

    public static final String dateTime() {
        Date var0 = new Date();
        return DateFormatUtils.format(var0, "yyyyMMdd");
    }

    public static final String dateTimeYY(Date date) {
        return DateFormatUtils.format(date, YY_MM_DD_HH_MM_SS);
    }

    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        } else {
            try {
                return parseDate(str.toString(), as);
            } catch (ParseException var2) {
                return null;
            }
        }
    }

    public static Date getServerStartDate() {
        long var0 = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(var0);
    }

    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int)((date2.getTime() - date1.getTime()) / 86400000L));
    }

    public static int differentSeconds(Date date1, Date date2) {
        return Math.abs((int)((date2.getTime() - date1.getTime()) / 1000L));
    }

    public static String getDatePoor(Date endDate, Date nowDate) {
        long var2 = 86400000L;
        long var4 = 3600000L;
        long var6 = 60000L;
        long var8 = endDate.getTime() - nowDate.getTime();
        long var10 = var8 / var2;
        long var12 = var8 % var2 / var4;
        long var14 = var8 % var2 % var4 / var6;
        return var10 + "天" + var12 + "小时" + var14 + "分钟";
    }

    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime var1 = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(var1.toInstant());
    }

    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime var1 = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime var2 = var1.atZone(ZoneId.systemDefault());
        return Date.from(var2.toInstant());
    }

    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    public static long getTimestampSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    public static String generateRandomHex(int length) {
        Random var1 = new Random();
        StringBuilder var2 = new StringBuilder(length);
        var2.append("D");

        for(int var3 = 1; var3 < length; ++var3) {
            int var4 = var1.nextInt(16);
            char var5 = Character.toUpperCase(Character.forDigit(var4, 16));
            var2.append(var5);
        }

        return var2.toString();
    }

    public static void main(String[] args) {
        Date var1 = dateTime(SS_MM_HH_DD_HH_YY, "434123181121");
        String var2 = dateTimeYY(var1);
        System.out.println(var2);
        String var3 = generateRandomHex(12);
        System.out.println(var3);
    }

    public static String strRemoveMs(String time) {
        Date var1 = dateTime(YYYY_MM_DD_HH_MM_SS_SSS, time);
        return parseDateToStr(YYYY_MM_DD_HH_MM_SS, var1);
    }

    public static Date dateRemoveMs(Date time) {
        String var1 = parseDateToStr(YYYY_MM_DD_HH_MM_SS, time);
        return dateTime(YYYY_MM_DD_HH_MM_SS, var1);
    }

    public static Date getDayBeginDate() {
        GregorianCalendar var0 = new GregorianCalendar();
        ((Calendar)var0).set(11, 0);
        ((Calendar)var0).set(12, 0);
        ((Calendar)var0).set(13, 0);
        ((Calendar)var0).set(14, 0);
        return ((Calendar)var0).getTime();
    }

    public static String getDayEnd() {
        GregorianCalendar var0 = new GregorianCalendar();
        ((Calendar)var0).set(11, 23);
        ((Calendar)var0).set(12, 59);
        ((Calendar)var0).set(13, 59);
        return dateTimeYY(((Calendar)var0).getTime());
    }

    public static Date getDayEndDate() {
        GregorianCalendar var0 = new GregorianCalendar();
        ((Calendar)var0).set(11, 23);
        ((Calendar)var0).set(12, 59);
        ((Calendar)var0).set(13, 59);
        return ((Calendar)var0).getTime();
    }
}
