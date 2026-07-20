package com.fastbee.icc.demo.accesscontrol.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.util.QRCodeUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-01 17:19
 * @Description:
 */
@Slf4j
public class ApplicationDemoTest {
    private ApplicationDemo applicationDemo;

    public ApplicationDemoTest() {
        applicationDemo = new ApplicationDemo();
    }

    /**
     * 测试远程开门
     */
    @Test
    public void testRemoteOpenDoor() {
        List<String> channelCodeList = new ArrayList<>();
        channelCodeList.add("1001631$7$0$0");
        applicationDemo.remoteOpenDoor(channelCodeList);
    }

    /**
     * 测试远程关门
     */
    @Test
    public void testRemoteCloseDoor() {
        List<String> channelCodeList = new ArrayList<>();
        channelCodeList.add("1001631$7$0$0");
        applicationDemo.remoteCloseDoor(channelCodeList);
    }

    /**
     * 测试设置停留开放模式
     */
    @Test
    public void testSetStayOpenMode() {
        List<String> channelCodeList = new ArrayList<>();
        channelCodeList.add("1001631$7$0$0");
        applicationDemo.setStayOpenMode(channelCodeList);
    }

    /**
     * 测试设置关闭模式
     */
    @Test
    public void testSetStayCloseMode() {
        List<String> channelCodeList = new ArrayList<>();
        channelCodeList.add("1001631$7$0$0");
        applicationDemo.setStayCloseMode(channelCodeList);
    }

    /**
     * 测试设置正常模式
     */
    @Test
    public void testSetStayNormalMode() {
        List<String> channelCodeList = new ArrayList<>();
        channelCodeList.add("1001631$7$0$0");
        applicationDemo.setStayNormalMode(channelCodeList);
    }

    /**
     * 测试获取业主二维码字符串并生成二维码图片
     */
    @Test
    public void testGenerateQRCode(){
        String personCode = "244031";
        String QRCode = applicationDemo.generateQRCode(personCode);
        //根据二维码字符串生成二维码图片
        if(QRCode!=null) {
            try {
                QRCodeUtil.generateQRCode(QRCode,300,300,"jpg","D://test1.jpg");
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage(),e);
            } catch (WriterException e) {
                e.printStackTrace();
                log.error(e.getMessage(),e);
            }
        }
    }

}
