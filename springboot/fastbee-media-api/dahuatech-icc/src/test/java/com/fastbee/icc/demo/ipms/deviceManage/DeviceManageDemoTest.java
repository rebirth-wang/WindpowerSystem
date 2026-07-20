package com.fastbee.icc.demo.ipms.deviceManage;

import lombok.Data;
import org.junit.Test;

import com.fastbee.icc.model.ipms.deviceManage.DevicePageRequest;

/**
 * @className DeviceManageDemoTest
 * @Author 355079
 * @Date 2024/12/10
 * @Description
 */
@Data
public class DeviceManageDemoTest {
    private DeviceManageDemo deviceManageDemo;
    public DeviceManageDemoTest(){
        deviceManageDemo = new DeviceManageDemo();
    }

    /**
     * 测试查询设备列表
     */
    @Test
    public void queryDeviceListTest() {
        DevicePageRequest devicePageRequest = new DevicePageRequest();
        devicePageRequest.setDeviceCode("1003071");
        devicePageRequest.setPageNum(1);
        devicePageRequest.setPageSize(20);
        deviceManageDemo.queryDeviceList(devicePageRequest);
    }

    /**
     * 测试全量查询道闸通道
     */
    @Test
    public void fullQuerySluiceChannelTest() {
        deviceManageDemo.fullQuerySluiceChannel();
    }

}
