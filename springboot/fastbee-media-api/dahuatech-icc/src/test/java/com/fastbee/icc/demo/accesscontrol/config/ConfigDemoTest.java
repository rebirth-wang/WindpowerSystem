package com.fastbee.icc.demo.accesscontrol.config;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fastbee.icc.model.accesscontrol.config.AddDoorGroupRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 19:51
 * @Description:
 */
public class ConfigDemoTest {
    private ConfigDemo configDemo;

    public ConfigDemoTest() {
        configDemo = new ConfigDemo();
    }

    /**
     * 测试添加门组
     */
    @Test
    public void testAddDoorGroup(){
        AddDoorGroupRequest addDoorGroupRequest = new AddDoorGroupRequest();
        addDoorGroupRequest.setName("测试添加门组");
        addDoorGroupRequest.setOrgCode("001016");
        List<AddDoorGroupRequest.DoorGroupDetail> doorGroupDetail = new ArrayList<>();
        AddDoorGroupRequest.DoorGroupDetail doorGroupDetail1 = new AddDoorGroupRequest.DoorGroupDetail();
        doorGroupDetail1.setChannelCode("1002600$7$0$0");
        doorGroupDetail.add(doorGroupDetail1);
        addDoorGroupRequest.setDoorGroupDetail(doorGroupDetail);
        configDemo.addDoorGroup(addDoorGroupRequest);
    }
}
