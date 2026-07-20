package com.fastbee.icc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-07 14:05
 * @Description:
 */
public class DateUtil {

    /**
     * yyyy-MM-dd HH:mm:ss格式日期字符串转换成10位时间戳
     * @return
     */
    public static Long convertDateToTimestamp(String dateStr){
        Long timestamp=null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(dateStr);
            // 转换为10位时间戳
            timestamp = date.getTime()/1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 10位时间戳转换成yyyy-MM-dd HH:mm:ss格式日期字符串
     * @param timestamp
     * @return
     */
    public static String convertTimestampToDate(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date(timestamp * 1000L));
        return dateStr;
    }

    /**
     * yyyy-MM-dd HH:mm:ss格式日期字符串转换成yyyy_MM_dd_HH_mm_ss格式
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static String convertToUnderlineDate(String dateStr){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        return dateFormat1.format(date);
    }


}
