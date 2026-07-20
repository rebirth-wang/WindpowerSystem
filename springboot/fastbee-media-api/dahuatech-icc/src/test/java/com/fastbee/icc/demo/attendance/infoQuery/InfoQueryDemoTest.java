package com.fastbee.icc.demo.attendance.infoQuery;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.attendance.infoQuery.GetRecordPageRequest;
import com.fastbee.icc.model.attendance.infoQuery.GetResultPageRequest;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-04-28 14:01
 * @Description: 信息查询测试类
 */
@Slf4j
public class InfoQueryDemoTest {
  private InfoQueryDemo infoQueryDemo;

  public InfoQueryDemoTest() {
      infoQueryDemo = new InfoQueryDemo();
  }

    /**
     * 测试打卡记录查询
     */
  @Test
  public void testGetAttendanceRecordPage(){
      GetRecordPageRequest getRecordPageRequest = new GetRecordPageRequest();
      getRecordPageRequest.setPageNum(1);
      getRecordPageRequest.setPageSize(10);
      getRecordPageRequest.setActionTimeBefore("2024-04-29 23:59:59");
      getRecordPageRequest.setActionTimeAfter("2024-04-29 00:00:00");
      infoQueryDemo.getAttendanceRecordPage(getRecordPageRequest);
  }

    /**
     * 测试分页获取考勤结果
     */
    @Test
    public void testGetAttendanceResultPage(){
        GetResultPageRequest getResultPageRequest = new GetResultPageRequest();
        getResultPageRequest.setPageNum(1);
        getResultPageRequest.setPageSize(10);
        getResultPageRequest.setDutyDateAfter("2024-04-29 00:00:00");
        getResultPageRequest.setDutyDateBefore("2024-04-29 23:59:59");
        infoQueryDemo.getAttendanceResultPage(getResultPageRequest);
    }
}
