package com.fastbee.icc.demo.video.ptzControl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fastbee.icc.model.video.ptzControl.*;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-09 19:34
 * @Description: 云台控制测试类
 */
public class PtzControlDemoTest {

    private PtzControlDemo ptzControlDemo;

    public PtzControlDemoTest() {
        ptzControlDemo = new PtzControlDemo();
    }

    /**
     * 测试操作摄像头
     */
    @Test
    public void testOperateCamera() {
        OperateCameraRequest operateCameraRequest = new OperateCameraRequest();
        OperateCameraRequest.Data data = new OperateCameraRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setOperateType("1");
        data.setDirect("1");
        data.setStep("2");
        data.setCommand("0");
        data.setExtend("");
        operateCameraRequest.setData(data);
        ptzControlDemo.operateCamera(operateCameraRequest);
    }

    /**
     * 测试直接操作云台
     */
    @Test
    public void testOperateDirect() {
        OperateDirectRequest operateDirectRequest = new OperateDirectRequest();
        OperateDirectRequest.Data data = new OperateDirectRequest.Data();
        data.setChannelId("1002539$1$0$0");
        data.setDirect("1");
        data.setStepX("2");
        data.setStepY("2");
        data.setCommand("0");
        data.setExtend("");
        operateDirectRequest.setData(data);
        ptzControlDemo.operateDirect(operateDirectRequest);
    }

    /**
     * 测试操作功能
     */
    @Test
    public void testOperateFunction() {
        OperateFunctionRequest operateFunctionRequest = new OperateFunctionRequest();
        OperateFunctionRequest.Data data = new OperateFunctionRequest.Data();
        data.setChannelId("1002539$1$0$10");
        data.setOperateType("21");
        data.setTarget("1");
        data.setCommand("1");
        data.setExtend("");
        operateFunctionRequest.setData(data);
        ptzControlDemo.operateFunction(operateFunctionRequest);
    }

    /**
     * 测试获取预置点信息
     */
    @Test
    public void testGetPresetPoints() {
        GetPresetPointsRequest getPresetPointsRequest = new GetPresetPointsRequest();
        GetPresetPointsRequest.Data data = new GetPresetPointsRequest.Data();
        data.setChannelId("1002430$1$0$0");
        getPresetPointsRequest.setData(data);
        ptzControlDemo.getPresetPoints(getPresetPointsRequest);
    }

    /**
     * 测试操作预置点
     */
    @Test
    public void testOperatePresetPoint() {
        OperatePresetPointRequest operatePresetPointRequest = new OperatePresetPointRequest();
        OperatePresetPointRequest.Data data = new OperatePresetPointRequest.Data();
        data.setChannelId("1002430$1$0$0");
        data.setPresetPointCode("8");
        data.setPresetPointName("测试");
        data.setOperateType("3");
        operatePresetPointRequest.setData(data);
        ptzControlDemo.operatePresetPoint(operatePresetPointRequest);
    }

    /**
     * 测试获取巡航列表功能
     */
    @Test
    public void testGetCruises() {
        GetCruisesRequest getCruisesRequest = new GetCruisesRequest();
        GetCruisesRequest.Data data = new GetCruisesRequest.Data();
        data.setChannelId("1002430$1$0$0");
        getCruisesRequest.setData(data);
        ptzControlDemo.getCruises(getCruisesRequest);
    }

    /**
     * 测试保存巡航线
     */
    @Test
    public void testSaveCruise() {
        SaveCruiseRequest saveCruiseRequest = new SaveCruiseRequest();
        SaveCruiseRequest.Data data = new SaveCruiseRequest.Data();
        data.setChannelId("1002430$1$0$0");

        List<CruiseInfo> cruises = new ArrayList<>();
        CruiseInfo cruise = new CruiseInfo();
        cruise.setId("4");
        cruise.setName("测试添加巡航线");
        cruise.setPointId("2");

        List<CruiseInfo.PrePoint> prePoints = new ArrayList<>();
        CruiseInfo.PrePoint prePoint1 = new CruiseInfo.PrePoint();
        prePoint1.setId("2156");
        prePoint1.setCode("8");
        prePoint1.setStay("5");
        prePoints.add(prePoint1);
        CruiseInfo.PrePoint prePoint2 = new CruiseInfo.PrePoint();
        prePoint2.setId("2158");
        prePoint2.setCode("9");
        prePoint2.setStay("5");
        prePoints.add(prePoint2);
        cruise.setPrePoints(prePoints);

        cruises.add(cruise);
        saveCruiseRequest.setData(data);
        saveCruiseRequest.getData().setCruises(cruises);
        ptzControlDemo.saveCruise(saveCruiseRequest);
    }

    /**
     * 测试删除巡航
     */
    @Test
    public void testDeleteCruise() {
        DeleteCruiseRequest deleteCruiseRequest = new DeleteCruiseRequest();
        DeleteCruiseRequest.Data data = new DeleteCruiseRequest.Data();
        data.setChannelId("1002430$1$0$0");
        deleteCruiseRequest.setData(data);
        ptzControlDemo.deleteCruise(deleteCruiseRequest);
    }


}
