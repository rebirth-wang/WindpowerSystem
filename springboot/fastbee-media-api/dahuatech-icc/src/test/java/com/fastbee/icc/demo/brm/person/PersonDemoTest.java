package com.fastbee.icc.demo.brm.person;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.brm.person.PersonPageRequest;
import com.fastbee.icc.util.FileUtil;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-12 16:47
 * @Description:基础资源人员相关接口调用测试
 */
@Slf4j
@Data
public class PersonDemoTest {
    private PersonDemo personDemo;

    public PersonDemoTest() {
        personDemo = new PersonDemo();
    }

    /**
     * 测试调用生成人员id接口
     */
    @Test
    public void testGeneratePersonId(){
        personDemo.generatePersonId();
    }

    /**
     * 测试上传图片接口
     * 上传的图片可通过返回的图片路径进行oss拼接进行查看
     */
    @Test
    public void testUploadImageByFile(){
        String filePath="D:"+ File.separator+"image.jpg";
        personDemo.uploadImageByFile(filePath);
    }

    /**
     * 测试上传图片接口
     * 上传的图片可通过返回的图片路径进行oss拼接进行查看
     */
    @Test
    public void testUploadImageByBase64(){
        String filePath="D:"+ File.separator+"image.jpg";
        try {
            String imageBase64 = FileUtil.convertImageToBase64WithHeader(filePath);
            personDemo.uploadImageByBase64(imageBase64);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

    /**
     * 测试获取RSA加密公钥
     */
    @Test
    public void testGetPublicKey(){
        personDemo.getPublicKey();
    }

    /**
     * 测试添加人员
     */
    @Test
    public void testAddPerson(){
        personDemo.addPerson();
    }

    /**
     * 测试更新人员
     */
    @Test
    public void testUpdatePerson(){
        personDemo.updatePerson();
    }

    /**
     * 测试人员信息分页查询
     */
    @Test
    public void testGetPersonPage(){
        PersonPageRequest personPageRequest = new PersonPageRequest();
        personPageRequest.setPageNum(1);
        personPageRequest.setPageSize(50);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(1);
        personPageRequest.setStatusList(statusList);
        personDemo.getPersonPage(personPageRequest);
    }

    /**
     * 获取全量人员信息
     */
    @Test
    public void testGetPersonList(){
        personDemo.getPersonList();
    }

    /**
     * 测试订阅人员的增删改（调一次获取人员全量信息方法后，订阅人员增删改可实现增量获取人员信息）
     */
    @Test
    public void testSubscribePersonOperation(){
        personDemo.subscribePersonOperation();
    }

}
