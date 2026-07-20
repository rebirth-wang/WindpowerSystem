package com.fastbee.icc.demo.visitor.realTimeMessage;

import org.junit.Test;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-19 10:47
 * @Description: 实时消息订阅测试
 */
public class RealTimeMessageDemoTest {
    private RealTimeMessageDemo realTimeMessageDemo;

    public RealTimeMessageDemoTest() {
        realTimeMessageDemo = new RealTimeMessageDemo();
    }

    /**
     * 订阅访客信息状态变化和访客通行记录实时消息
     */
    @Test
    public void testSubscribeVisitorInfo(){
        realTimeMessageDemo.subscribeVisitorInfo();
    }
}
