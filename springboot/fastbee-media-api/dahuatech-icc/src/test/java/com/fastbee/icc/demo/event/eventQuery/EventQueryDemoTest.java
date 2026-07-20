package com.fastbee.icc.demo.event.eventQuery;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.event.eventQuery.AlarmRecordCountRequest;
import com.fastbee.icc.model.event.eventQuery.AlarmRecordPageRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-11 14:29
 * @Description: 测试事件订阅
 */
@Slf4j
public class EventQueryDemoTest {
    private EventQueryDemo eventQueryDemo;

    public EventQueryDemoTest() {
        eventQueryDemo = new EventQueryDemo();
    }

    /**
     * 测试获取告警记录数量
     */
    @Test
    public void testGetAlarmRecordCount() {
        AlarmRecordCountRequest alarmRecordCountRequest = new AlarmRecordCountRequest();
        alarmRecordCountRequest.setAlarmStartDateString("2024-06-11 00:00:00");
        alarmRecordCountRequest.setAlarmEndDateString("2024-06-11 23:59:59");
        eventQueryDemo.getAlarmRecordCount(alarmRecordCountRequest);
    }

    /**
     * 测试获取告警记录页面
     */
    @Test
    public void testGetAlarmRecordPage() {
        AlarmRecordPageRequest alarmRecordPageRequest = new AlarmRecordPageRequest();
        alarmRecordPageRequest.setPageNum(1);
        alarmRecordPageRequest.setPageSize(20);
        alarmRecordPageRequest.setAlarmStartDateString("2024-06-11 00:00:00");
        alarmRecordPageRequest.setAlarmEndDateString("2024-06-11 23:59:59");
        eventQueryDemo.getAlarmRecordPage(alarmRecordPageRequest);
    }
}
