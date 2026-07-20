package com.fastbee.icc.demo.event.eventSubscribe;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-11 10:51
 * @Description: 事件订阅测试类
 */
@Slf4j
@Data
public class SubscribeDemoTest {
    private SubscribeDemo subscribeDemo;
    public SubscribeDemoTest() {
        subscribeDemo = new SubscribeDemo();
    }

    /**
     * 测试直接通过json字符串订阅事件
     */
    @Test
    public void testSubscribeEventByJson() {
        String jsonRequestBody="{\n" +
                "    \"param\": {\n" +
                "        \"monitors\": [\n" +
                "            {\n" +
                "                \"monitor\": \"http://10.35.111.10:8010/eventReceiveMsg/save\",\n" +
                "                \"monitorType\": \"url\",\n" +
                "                \"events\": [\n" +
                "                    {\n" +
                "                        \"category\": \"alarm\",\n" +
                "                        \"subscribeAll\": 1,\n" +
                "                        \"domainSubscribe\": 2,\n" +
                "                        \"authorities\": [\n" +
                "                            {\n" +
                "                                \"types\": [\n" +
                "                                    \"51\",\n" +
                "                                    \"61\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"category\": \"business\",\n" +
                "                        \"subscribeAll\": 1,\n" +
                "                        \"domainSubscribe\": 2,\n" +
                "                        \"authorities\": [\n" +
                "                            {\n" +
                "                                \"types\": [\n" +
                "                                    \"car.capture\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"subsystem\": {\n" +
                "            \"subsystemType\": 0,\n" +
                "            \"name\": \"10.35.111.10_8010\",\n" +
                "            \"magic\": \"10.35.111.10_8010\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        subscribeDemo.subscribeEventByJson(jsonRequestBody);
    }

    /**
     * 测试订阅报警事件
     */
    @Test
    public void testSubscribeAlarmEvent(){
        subscribeDemo.subscribeAlarmEvent();
    }

    /**
     * 测试订阅业务事件
     */
    @Test
    public void testSubscribeBusinessEvent(){
        subscribeDemo.subscribeBusinessEvent();
    }

    /**
     * 测试订阅感知事件
     */
    @Test
    public void testSubscribePerceptionEvent(){
        subscribeDemo.subscribePerceptionEvent();
    }

    /**
     * 测试订阅状态事件
     */
    @Test
    public void testSubscribeStateEvent(){
        subscribeDemo.subscribeStateEvent();
    }

    /**
     * 测试订阅报警事件和业务事件
     */
    @Test
    public void testAlarmAndBusiness(){
        subscribeDemo.subscribeAlarmAndBusinessEvent();
    }

    /**
     * 取消事件订阅
     */
    @Test
    public void testCancelSubscribe(){
        subscribeDemo.cancelSubscribe("10.54.20.33_8003");
    }

    /**
     * 测试事件订阅列表查询
     */
    @Test
    public void testGetSubscribeList(){
        subscribeDemo.getSubscribeList("alarm");
    }
}
