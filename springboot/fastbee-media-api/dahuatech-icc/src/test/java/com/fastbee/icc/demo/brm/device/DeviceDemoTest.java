package com.fastbee.icc.demo.brm.device;

import java.util.Arrays;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.brm.device.ChannelPageRequest;
import com.fastbee.icc.model.brm.device.DevicePageRequest;
import com.fastbee.icc.model.brm.device.DeviceTreeRequest;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-12 16:47
 * @Description:基础资源设备相关接口调用测试
 */
@Slf4j
@Data
public class DeviceDemoTest {
    private DeviceDemo deviceDemo;

    public DeviceDemoTest() {
        deviceDemo = new DeviceDemo();
    }

    /**
     * 测试分页获取设备信息
     */
    @Test
    public void testGetDevicePage(){
        DevicePageRequest devicePageRequest = new DevicePageRequest();
        devicePageRequest.setPageNum(1);
        devicePageRequest.setPageSize(50);
        deviceDemo.getDevicePage(devicePageRequest);
    }

    /**
     * 测试获取全量设备信息
     */
    @Test
    public void testGetDeviceList(){
        deviceDemo.getDeviceList();
    }

    /**
     * 测试订阅设备的增删改（调一次获取设备全量信息方法后，订阅设备增删改可实现增量获取设备信息）
     */
    @Test
    public void testSubscribeDeviceOperation(){
        deviceDemo.subscribeDeviceOperation();
    }

    /**
     * 测试分页获取通道信息
     */
    @Test
    public void testGetChannelPage(){
        ChannelPageRequest channelPageRequest = new ChannelPageRequest();
        channelPageRequest.setPageNum(1);
        channelPageRequest.setPageSize(10);
        //查询视频通道
        channelPageRequest.setUnitTypeList(Arrays.asList(1));
        deviceDemo.getChannelPage(channelPageRequest);
    }

    /**
     * 测试获取设备树
     */
    @Test
    public void testGetDeviceTree() {
        DeviceTreeRequest deviceTreeRequest = new DeviceTreeRequest();
        deviceTreeRequest.setType("001;;1");
        deviceTreeRequest.setId("001001");
        deviceTreeRequest.setCheckStat(1);
        deviceDemo.getDeviceTree(deviceTreeRequest);
    }




}
