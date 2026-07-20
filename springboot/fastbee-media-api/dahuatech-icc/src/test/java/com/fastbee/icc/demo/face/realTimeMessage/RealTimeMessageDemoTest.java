package com.fastbee.icc.demo.face.realTimeMessage;

import org.junit.Test;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-17 09:11
 * @Description: 实时消息测试类
 */
public class RealTimeMessageDemoTest {
    private RealTimeMessageDemo realTimeMessageDemo;

    public RealTimeMessageDemoTest() {
        realTimeMessageDemo = new RealTimeMessageDemo();
    }

    /**
     * 测试订阅黑名单库报警
     */
    @Test
    public void testSubscribeBlacklistAlarm(){
        realTimeMessageDemo.subscribeBlacklistAlarm();
    }

}
