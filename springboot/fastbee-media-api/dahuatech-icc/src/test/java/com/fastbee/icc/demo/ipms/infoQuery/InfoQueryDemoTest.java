package com.fastbee.icc.demo.ipms.infoQuery;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordCountRequest;
import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordPageRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-07 15:39
 * @Description: 信息查询测试
 */
@Slf4j
@Data
public class InfoQueryDemoTest {
    private InfoQueryDemo infoQueryDemo;

    public InfoQueryDemoTest() {
        infoQueryDemo = new InfoQueryDemo();
    }

    /**
     * 测试统计车辆进出总数
     */
    @Test
    public void testGetInAndOutRecordCount(){
        InAndOutRecordCountRequest inAndOutRecordCountRequest = new InAndOutRecordCountRequest();
        inAndOutRecordCountRequest.setEnterTimeStrLeft("2025-07-01 00:00:00");
        inAndOutRecordCountRequest.setEnterTimeStrRight("2025-07-31 23:59:00");
        infoQueryDemo.getInAndOutRecordCount(inAndOutRecordCountRequest);
    }

    /**
     * 测试车辆进出数据
     */
    @Test
    public void testGetInAndOutRecordPage(){
        InAndOutRecordPageRequest inAndOutRecordPageRequest = new InAndOutRecordPageRequest();
        inAndOutRecordPageRequest.setPageNum(1);
        inAndOutRecordPageRequest.setPageSize(20);
        inAndOutRecordPageRequest.setEnterTimeStrLeft("2025-07-01 00:00:00");
        inAndOutRecordPageRequest.setEnterTimeStrRight("2025-07-31 23:59:00");
        infoQueryDemo.getInAndOutRecordPage(inAndOutRecordPageRequest);
    }
}
