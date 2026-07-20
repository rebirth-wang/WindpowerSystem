package com.fastbee.iot.util;

import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.fastjson2.JSON;

import com.fastbee.common.constant.MagicValueConstants;
import com.fastbee.common.constant.SceneModelConstants;
import com.fastbee.iot.model.scenemodel.JobCronCycleVO;

/**
 * @author gsb
 * @date 2024/6/21 8:30
 */
public class JobCronUtils {


    /**
     * 处理时间周期Cron表达式
     * @param cycleType 时间周期类型
     * @param: cycle 表达式
     * @return com.fastbee.iot.model.scenemodel.SceneModelTagCycleVO
     */
    public static JobCronCycleVO handleCronCycle(Integer cycleType, String cycle) {
        String cron = "";
        String desc = "";
        List<JobCronCycleVO> list = JSON.parseArray(cycle, JobCronCycleVO.class);
        JobCronCycleVO jobCronCycleVO = list.get(0);
        switch (cycleType) {
            // 周期循环
            case 1:
                // 几分钟运算一次
                if (null != jobCronCycleVO.getInterval()) {
                    if (jobCronCycleVO.getInterval() >= MagicValueConstants.VALUE_3600) {
                        int hour = jobCronCycleVO.getInterval() / MagicValueConstants.VALUE_3600;
                        cron = "0 0 0/" + hour +" * * ?";
                        desc = "每" + hour + "小时";
                    } else {
                        int min = jobCronCycleVO.getInterval() / MagicValueConstants.VALUE_60;
                        cron = "0 0/" + min + " * * * ?";
                        desc = "每" + min + "分钟";
                    }
                }
                // 每小时运算一次
                if (null != jobCronCycleVO.getType()
                        && SceneModelConstants.CYCLE_HOUR.equals(jobCronCycleVO.getType())) {
                    cron = "0 0 0/1 * * ?";
                    desc = "每1小时";
                }
                // 每天几时运算一次
                if (SceneModelConstants.CYCLE_DAY.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getTime()) {
                    cron = "0 0 " + jobCronCycleVO.getTime() + " * * ?";
                    desc = "每天" + jobCronCycleVO.getTime() + "时";
                }
                // 每周周几几时运算一次
                if (SceneModelConstants.CYCLE_WEEK.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getWeek() && null != jobCronCycleVO.getTime()) {
                    cron = "0 0 " + jobCronCycleVO.getTime() + " ? * " + jobCronCycleVO.getWeek();
                    desc = "每周周" + jobCronCycleVO.getWeek() + " " + jobCronCycleVO.getTime() + "时";
                }
                // 每月第几日几时运算一次
                if (SceneModelConstants.CYCLE_MONTH.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getDay() && null != jobCronCycleVO.getTime()) {
                    cron = "0 0 " + jobCronCycleVO.getTime() + " " + jobCronCycleVO.getDay() + " * ?";
                    desc = "每月第" + jobCronCycleVO.getDay() + "日" + jobCronCycleVO.getTime() + "时";
                }
                break;
            // 自定义时间段，取结尾作为时间节点
            case 2:
                // 每日几时
                if (SceneModelConstants.CYCLE_DAY.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getToTime()) {
                    cron = "0 0 " + jobCronCycleVO.getToTime() + " * * ?";
                }
                // 每周周几运算一次
                if (SceneModelConstants.CYCLE_WEEK.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getToWeek() && null != jobCronCycleVO.getToTime()) {
                    cron = "0 0 " + jobCronCycleVO.getToTime() + " ? * " + jobCronCycleVO.getToWeek();
                }
                // 每月几号几时运算一次
                if (SceneModelConstants.CYCLE_MONTH.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getToDay() && null != jobCronCycleVO.getToTime()) {
                    cron = "0 0 " + jobCronCycleVO.getToTime() + " " + jobCronCycleVO.getToDay() + " * ?";
                }
                break;
            // 固定时间
            case 3:
                // 解析字符串为LocalDateTime对象
                LocalDateTime dateTime = jobCronCycleVO.getEndTime();
                int second = dateTime.getSecond();
                int minute = dateTime.getMinute();
                int hour = dateTime.getHour();
                int day = dateTime.getDayOfMonth();
                int month = dateTime.getMonthValue();
                cron = String.format("%d %d %d %d %d ?", second, minute, hour, day, month);
                break;
            default:
                break;
        }
        jobCronCycleVO.setCron(cron);
        jobCronCycleVO.setDesc(desc);
        return jobCronCycleVO;
    }

    /**
     * 处理时间周期 开始、结束时间
     * @param cycleType 时间周期类型
     * @param: cycle 表达式
     * @return com.fastbee.iot.model.scenemodel.SceneModelTagCycleVO
     */
    public static JobCronCycleVO handleTimeCycle(Integer cycleType, String cycle, LocalDateTime executeTime) {
        LocalDateTime beginTime = null;
        LocalDateTime endTime = executeTime.withSecond(0);
        List<JobCronCycleVO> list = JSON.parseArray(cycle, JobCronCycleVO.class);
        JobCronCycleVO jobCronCycleVO = list.get(0);
        switch (cycleType) {
            // 周期循环
            case 1:
                // 几分钟运算一次
                if (null != jobCronCycleVO.getInterval()) {
                    int min = jobCronCycleVO.getInterval() / MagicValueConstants.VALUE_60;
                    beginTime = endTime.minusMinutes(min);
                }
                // 每小时运算一次
                if (null != jobCronCycleVO.getType()
                        && SceneModelConstants.CYCLE_HOUR.equals(jobCronCycleVO.getType())) {
                    beginTime = endTime.minusHours(1);
                }
                // 每天几时运算一次
                if (SceneModelConstants.CYCLE_DAY.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getTime()) {
                    beginTime = endTime.minusDays(1);
                }
                // 每周周几几时运算一次
                if (SceneModelConstants.CYCLE_WEEK.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getWeek() && null != jobCronCycleVO.getTime()) {
                    beginTime = endTime.minusWeeks(1);
                }
                // 每月第几日几时运算一次
                if (SceneModelConstants.CYCLE_MONTH.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getDay() && null != jobCronCycleVO.getTime()) {
                    beginTime = endTime.minusMonths(1);
                }
                break;
            // 自定义时间段，取开始时间
            case 2:
                // 每日几时
                if (SceneModelConstants.CYCLE_DAY.equals(jobCronCycleVO.getType())) {
                    if (SceneModelConstants.CYCLE_TO_TYPE_NOW_DAY.equals(jobCronCycleVO.getToType())) {
                        beginTime = endTime.withHour(jobCronCycleVO.getTime());
                    } else if (SceneModelConstants.CYCLE_TO_TYPE_SECOND_DAY.equals(jobCronCycleVO.getToType())) {
                        beginTime = endTime.minusDays(1).withHour(jobCronCycleVO.getTime());
                    }
                }
                // 每周周几运算一次
                if (SceneModelConstants.CYCLE_WEEK.equals(jobCronCycleVO.getType())) {
                    int weekDay = jobCronCycleVO.getToWeek() - jobCronCycleVO.getWeek();
                    beginTime = endTime.minusDays(weekDay).withHour(jobCronCycleVO.getTime());
                }
                // 每月几号几时运算一次
                if (SceneModelConstants.CYCLE_MONTH.equals(jobCronCycleVO.getType())
                        && null != jobCronCycleVO.getToDay() && null != jobCronCycleVO.getToTime()) {
                    beginTime = endTime.withDayOfMonth(jobCronCycleVO.getDay()).withHour(jobCronCycleVO.getTime());
                }
                break;
            case 3:
                beginTime = jobCronCycleVO.getBeginTime();
                endTime = jobCronCycleVO.getEndTime();
            default:
                break;
        }
        jobCronCycleVO.setBeginTime(beginTime);
        jobCronCycleVO.setEndTime(endTime);
        return jobCronCycleVO;
    }
}
