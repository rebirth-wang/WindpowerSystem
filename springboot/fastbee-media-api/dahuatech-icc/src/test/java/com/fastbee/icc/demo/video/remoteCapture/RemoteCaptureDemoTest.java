package com.fastbee.icc.demo.video.remoteCapture;

import com.dahuatech.icc.util.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.video.remoteCapture.RemoteCaptureResponse;
import com.fastbee.icc.model.video.remoteCapture.RemoteCaptureResult;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-12 14:59
 * @Description: 设备远程抓图测试
 */
@Slf4j
public class RemoteCaptureDemoTest {
    private RemoteCaptureDemo remoteCaptureDemo;

    public RemoteCaptureDemoTest() {
        remoteCaptureDemo = new RemoteCaptureDemo();
    }

    /**
     * 测试远程设备抓图
     */
    @Test
    public void testRemoteCapture(){
        RemoteCaptureResponse remoteCaptureResponse = remoteCaptureDemo.remoteCapture(111l,"1002430$1$0$0");
        if(remoteCaptureResponse.getCode().equals(1000)) {
            RemoteCaptureResult captureResult = BeanUtil.toBean(remoteCaptureResponse.getData(), RemoteCaptureResult.class);
            String imagePath = captureResult.getParams().getPicInfo();
            log.info("远程设备抓图获取的图片地址：{}", imagePath);
        }
    }
}
