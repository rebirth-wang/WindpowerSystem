package com.fastbee.icc.demo.alarm.alarmOperate;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.alarm.alarmOperate.AlarmOperateRequest;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-04-28 11:58:00
 * @Description: 远程控制测试类
 */

@Slf4j
public class AlarmOperateDemoTest {
    private  AlarmOperateDemo alarmOperateDemo;

    public AlarmOperateDemoTest() {
        alarmOperateDemo = new AlarmOperateDemo();
    }

    /**
     * 测试报警主机操作控制(用于处理资源绑定数据)
     */
    @Test
    public void testAlarmHostOperate(){
        AlarmOperateRequest alarmOperateRequest = new AlarmOperateRequest();
        alarmOperateRequest.setOperate(1);
        alarmOperateRequest.setDelayTime(1l);

        List<AlarmOperateRequest.NodeInfo> nodeInfoList = new ArrayList<>();
        AlarmOperateRequest.NodeInfo nodeInfo1 = new AlarmOperateRequest.NodeInfo();
        nodeInfo1.setNodeCode("1000001");
        nodeInfo1.setNodeName("111");
        nodeInfoList.add(nodeInfo1);
        alarmOperateRequest.setNodeInfoList(nodeInfoList);

        alarmOperateDemo.operateAlarmHost(alarmOperateRequest);

    }
}
