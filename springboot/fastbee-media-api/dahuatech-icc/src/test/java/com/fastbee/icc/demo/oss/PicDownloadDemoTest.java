package com.fastbee.icc.demo.oss;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-26 20:26
 * @Description: 图片下载测试类
 */
@Slf4j
public class PicDownloadDemoTest {
    private static Logger logger = LoggerFactory.getLogger(PicDownloadDemoTest.class);
    private PicDownloadDemo picDownloadDemo;

    public PicDownloadDemoTest() {
        this.picDownloadDemo = new PicDownloadDemo();
    }

    /**
     * 测试https协议下载图片
     */
    @Test
    public void testHttpsImageDownload(){
        String imageUrl="a207d588-5c17-11ee-8a3c-f01090fde19a/20240422/1/32c4011a-0052-11ef-87db-f01090fde19a.jpg";
        String imageFullPath=picDownloadDemo.getHttpsImageFullPath(imageUrl);
        picDownloadDemo.download(imageFullPath,"D://test");
    }

    /**
     * 测试http协议下载图片
     */
    @Test
    public void testHttpImageDownload() {
        String imageUrl="a207d588-5c17-11ee-8a3c-f01090fde19a/20240331/1/b1451763-ef2d-11ee-952a-f01090fde19a.jpg";
        String imageFullPath=picDownloadDemo.getHttpImageFullPath(imageUrl);
        picDownloadDemo.download(imageFullPath,"D://test");
    }

    /**
     * 测试https协议下载图片流
     */
    @Test
    public void testHttpsImageStreamDownload()  {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("D://test111.jpg");
            String imageUrl="a207d588-5c17-11ee-8a3c-f01090fde19a/20240331/1/b1451763-ef2d-11ee-952a-f01090fde19a.jpg";
            String imageFullPath=picDownloadDemo.getHttpsImageFullPath(imageUrl);
            ByteArrayOutputStream byteArrayOutputStream = picDownloadDemo.download(imageFullPath);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 测试http协议下载获取图片流
     * @throws IOException
     */
    @Test
    public void testHttpImageStreamDownload() throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String path = System.getProperty("user.dir");
        FileOutputStream fileOutputStream = new FileOutputStream(path + "/target/" + dateFormat.format(date) + ".jpg");
        String imageUrl="a207d588-5c17-11ee-8a3c-f01090fde19a/20240904/1/04cb33cb-6aa0-11ef-a1b9-f01090fde19a.jpg";
        String imageFullPath=picDownloadDemo.getHttpImageFullPath(imageUrl);
        ByteArrayOutputStream byteArrayOutputStream = picDownloadDemo.download(imageFullPath);
        fileOutputStream.write(byteArrayOutputStream.toByteArray());
        fileOutputStream.close();
    }

    /**
     * 测试将图片转换成base64格式字符串
     */
    @Test
    public void testGetImageBase(){
        String imagePath="D://test111.jpg";
        String imageBase = PicDownloadDemo.getImageBase(imagePath);
        logger.info("Base64 image:{}",imageBase);
    }

}
