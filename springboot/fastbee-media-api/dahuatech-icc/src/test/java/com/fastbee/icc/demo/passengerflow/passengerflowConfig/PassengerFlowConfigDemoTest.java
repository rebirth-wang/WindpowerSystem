package com.fastbee.icc.demo.passengerflow.passengerflowConfig;

import org.junit.Test;

import com.fastbee.icc.model.passengerflow.passengerflowConfig.RegionConfigPageRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-05-08 13:46
 * @Description: 客流配置测试类
 */
public class PassengerFlowConfigDemoTest {
    private PassengerFlowConfigDemo passengerFlowConfigDemo;

    public PassengerFlowConfigDemoTest() {
        passengerFlowConfigDemo = new PassengerFlowConfigDemo();
    }

    /**
     * 测试分页查询区域配置列表
     */
    @Test
    public void testGetRegionConfigPage(){
        RegionConfigPageRequest regionConfigPageRequest = new RegionConfigPageRequest();
        regionConfigPageRequest.setPageNum(1);
        regionConfigPageRequest.setPageSize(2);
        passengerFlowConfigDemo.getRegionConfigPage(regionConfigPageRequest);
    }
}
