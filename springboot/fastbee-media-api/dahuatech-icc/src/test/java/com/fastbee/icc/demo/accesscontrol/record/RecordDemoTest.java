package com.fastbee.icc.demo.accesscontrol.record;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.accesscontrol.record.QueryRecordRequest;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-04-01 11:42
 * @Description: 门禁事件测试类
 */
@Slf4j
public class RecordDemoTest {
    private RecordDemo recordDemo;

    public RecordDemoTest() {
        recordDemo = new RecordDemo();
    }

    /**
     * 测试门禁记录数量查询
     */
    @Test
    public void testGetRecordCount(){
        QueryRecordRequest queryRecordRequest = new QueryRecordRequest();
        queryRecordRequest.setStartSwingTime("2024-04-01 00:00:00");
        queryRecordRequest.setEndSwingTime("2024-04-01 23:59:59");
        recordDemo.getRecordCount(queryRecordRequest);
    }

    /**
     * 测试门禁记录分页查询
     */
    @Test
    public void testGetRecordPage(){
        QueryRecordRequest queryRecordRequest = new QueryRecordRequest();
        queryRecordRequest.setStartSwingTime("2024-04-01 00:00:00");
        queryRecordRequest.setEndSwingTime("2024-04-01 23:59:59");
        queryRecordRequest.setPageNum(1);
        queryRecordRequest.setPageSize(20);
        recordDemo.getRecordPage(queryRecordRequest);
    }

    /**
     * 测试获取指定时间内所有门禁记录
     */
    @Test
    public void testGetRecordList(){
        QueryRecordRequest queryRecordRequest = new QueryRecordRequest();
        queryRecordRequest.setStartSwingTime("2024-04-01 00:00:00");
        queryRecordRequest.setEndSwingTime("2024-04-01 23:59:59");
        queryRecordRequest.setPageNum(1);
        queryRecordRequest.setPageSize(20);
        recordDemo.getRecordList(queryRecordRequest);
    }

    /**
     * 测试订阅门禁记录和补采记录（可实现增量获取门禁记录）
     */
    @Test
    public void testSubscribeAccessControlRecord(){
        recordDemo.subscribeAccessControlRecord();
    }
}
