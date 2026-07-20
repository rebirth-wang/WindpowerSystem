package com.fastbee.icc.demo.face.faceSearch;

import java.io.IOException;
import java.util.*;

import com.dahuatech.hutool.core.util.StrUtil;
import com.dahuatech.hutool.json.JSONArray;
import com.dahuatech.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.fastbee.icc.model.face.faceSearch.*;
import com.fastbee.icc.util.FileUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-15 11:28
 * @Description: 以图搜图测试类
 */
@Slf4j
public class FaceSearchDemoTest {
    private FaceSearchDemo faceSearchDemo;

    public FaceSearchDemoTest() {
        faceSearchDemo = new FaceSearchDemo();
    }

    /**
     * 测试以图搜图前置条件（查询设备树）
     */
    @Test
    public void testGetDeviceTree(){
        GetDeviceTreeRequest getDeviceTreeRequest = new GetDeviceTreeRequest();
        getDeviceTreeRequest.setType("001;1,62");
        getDeviceTreeRequest.setId("001");
        getDeviceTreeRequest.setCheckStat("1");
        getDeviceTreeRequest.setCapacity("1_00000000000000000000000100000000");
        GetDeviceTreeResponse getDeviceTreeResponse = faceSearchDemo.getDeviceTree(getDeviceTreeRequest);
        if(getDeviceTreeResponse.getCode().equals("0")&& !StringUtils.isEmpty(getDeviceTreeResponse.getData())){
            JSONArray array = new JSONArray(getDeviceTreeResponse.getData());
            List<GetDeviceTreeResult> deviceList = JSONUtil.toList(array,GetDeviceTreeResult.class);
            log.info("返回的json字符串转换成对象：{}", JSONUtil.toJsonStr(deviceList));
        }
    }

    /**
     * 测试抓拍库以图搜图前置条件（根据设备编码获取通道列表）
     */
    @Test
    public void testGetChannelCodeList(){
        GetChannelCodeListRequest getChannelCodeListRequest = new GetChannelCodeListRequest();
        getChannelCodeListRequest.setDeviceCodeList("1002589,1002183");
        faceSearchDemo.getChannelCodeList(getChannelCodeListRequest);
    }

    /**
     * 测试人像库搜图前置条件（根据设备编码查询人像库）
     */
    @Test
    public void testGetGroup(){
        GetGroupRequest getGroupRequest = new GetGroupRequest();
        getGroupRequest.setDeviceCodes(Arrays.asList("1002589"));
        getGroupRequest.setGroupNames("xxx");
        faceSearchDemo.getGroup(getGroupRequest);
    }


    /**
     * 测试以图搜图-同步
     */
    @Test
    public void testFaceSearchSync(){
        FaceSearchSyncRequest faceSearchSyncRequest = new FaceSearchSyncRequest();
        try {
            String base64ImageString = FileUtil.convertImageToBase64("D://image.jpg");
            faceSearchSyncRequest.setBase64Img(base64ImageString);
            faceSearchSyncRequest.setThreshold(60);
            faceSearchSyncRequest.setGroupIds(Arrays.asList("100171"));
            faceSearchSyncRequest.setDeviceCodes(Arrays.asList("1002589"));
            faceSearchDemo.faceSearchSync(faceSearchSyncRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试以图搜图-同步
     */
    @Test
    public void testFaceSearchAsync(){
        try {
            //第一步：创建人像以图搜图任务
            CreateSearchTaskRequest createSearchTaskRequest = new CreateSearchTaskRequest();
            createSearchTaskRequest.setSearchGroupType(1);
            String base64ImageString = FileUtil.convertImageToBase64("D://image.jpg");
            createSearchTaskRequest.setBase64Img(base64ImageString);
            createSearchTaskRequest.setThreshold(60);
            createSearchTaskRequest.setDevType(1);
            createSearchTaskRequest.setStartTime("2024-04-16 00:00:00");
            createSearchTaskRequest.setEndTime("2024-04-16 23:59:59");
            createSearchTaskRequest.setOrdered(4);
            Map<String,List<String>> deviceChannels = new HashMap<>();
            List<String> channels = new ArrayList<>();
            channels.add("1002589$1$0$0");
            channels.add("1002589$1$0$1");
            deviceChannels.put("1002589",channels);
            createSearchTaskRequest.setDeviceChannels(deviceChannels);
            CreateSearchTaskResponse createSearchTaskResponse = faceSearchDemo.createSearchTask(createSearchTaskRequest);
            if(!createSearchTaskResponse.getCode().equals("0")&&StrUtil.isEmpty(createSearchTaskResponse.getData())){
                log.error("创建人像以图搜图任务失败");
            }
            String sessionId = createSearchTaskResponse.getData();

            //第二步：查询以图搜图任务进度
            QueryTaskProgressResponse queryTaskProgressResponse =null;
            int num = 0;
            //这里是10分钟还未完成任务就退出循环，现场可根据实际情况设置时间
            while (queryTaskProgressResponse==null ||
                    (queryTaskProgressResponse.getCode().equals("0") && queryTaskProgressResponse.getData().getProgress().floatValue() < 100.0 && num <=60)){
                queryTaskProgressResponse = faceSearchDemo.queryTaskProgress(sessionId);
                if (!queryTaskProgressResponse.getCode().equals("0")) {
                    log.error("查询以图搜图任务进度失败");
                    break;
                }
                if (queryTaskProgressResponse.getData().getProgress().floatValue() < 100.0) {
                    try {
                        //等待10秒，循环调用确认任务是否完成
                        Thread.sleep(10000);
                        num++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
            //第三步获取搜索结果
            GetTaskResultRequest getTaskResultRequest = new GetTaskResultRequest();
            getTaskResultRequest.setSearchGroupType(1);
            getTaskResultRequest.setPageNum(1);
            getTaskResultRequest.setPageSize(10);
            getTaskResultRequest.setOrdered(1);
            getTaskResultRequest.setSessionId(sessionId);
            GetTaskResultResponse getTaskResultResponse = faceSearchDemo.getTaskResult(getTaskResultRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
