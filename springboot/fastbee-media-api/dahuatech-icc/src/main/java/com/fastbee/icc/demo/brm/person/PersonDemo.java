package com.fastbee.icc.demo.brm.person;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.brm.person.*;
import com.fastbee.icc.model.event.eventSubcribe.SubscribeRequest;
import com.fastbee.icc.util.RSAUtil;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-12 16:04
 * @Description: 基础资源人员相关接口调用demo演示
 */
@Slf4j
@Data
public class PersonDemo {

    /**
     * 获取人员id
     * 新增人员的id参数不支持自定义，需调获取人员id接口获取
     * @return 人员id
     */
    public Long generatePersonId(){
        Long id=null;
        PersonGenerateIdResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            response =HttpUtils.executeJson("/evo-apigw/evo-brm/1.0.0/person/generate-id", null,null, Method.GET , config, PersonGenerateIdResponse.class);
            log.info("PersonDemo,generateId,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(response.isSuccess()) {
            id = response.getData().getId();
            log.info("PersonDemo,generateId,personId:{}",response.getData().getId());
        }else{
            log.info("获取人员id失败:{}",response.getErrMsg());
        }
        return id;
    }

    /**
     * 上传图片（适用于icc平台5.0.14及以下版本）
     * @param filePath 上传图片路径
     * @return fileUrl 图片url
     * @throws ClientException
     */
    public String uploadImageByFile(String filePath){
        String fileUrl=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        UploadImageResponse response=null;
        try {
            Map map = new HashMap();
            File file = new File(filePath);
            map.put("file",file);
            response = HttpUtils.executeForm("/evo-apigw/evo-brm/1.2.0/person/upload/img", map,null, Method.POST , config, UploadImageResponse.class);
            log.info("PersonDemo,uploadImageByFile,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(response.isSuccess()) {
            fileUrl = response.getData().getFileUrl();
            log.info("PersonDemo,uploadImageByFile,fileUrl:{}", response.getData().getFileUrl());
        }else {
            log.info("图片上传失败:{}",response.getErrMsg());
        }
        return fileUrl;
    }

    /**
     * 上传图片（适用于icc平台5.0.15及以上版本）
     * @param imageBase64 图片base64字符串
     * @return fileUrl 图片url
     * @throws ClientException
     */
    public String uploadImageByBase64(String imageBase64){
        String fileUrl=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        UploadImageResponse response=null;
        try {
            Map map = new HashMap();
            map.put("imageBase64",imageBase64);
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.0.0/person/subsystem/third/upload/img", map,null, Method.POST , config, UploadImageResponse.class);
            log.info("PersonDemo,uploadImageByBase64,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(response.isSuccess()) {
            fileUrl = response.getData().getFileUrl();
            log.info("PersonDemo,uploadImageByBase64,fileUrl:{}", response.getData().getFileUrl());
        }else {
            log.info("图片上传失败:{}",response.getErrMsg());
        }
        return fileUrl;
    }

    /**
     * 获取RSA加密公钥
     * @return
     */
    public GetPublicKeyResponse getPublicKey(){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GetPublicKeyResponse response=null;
        try {
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/encrypt/public-key", null,null, Method.GET , config, GetPublicKeyResponse.class);
            log.info("PersonDemo,getPublicKey,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("获取RSA加密公钥失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 添加人员
     * @return 人员id
     */
    public Long addPerson(){
        PersonAddRequest personAddRequest=new PersonAddRequest();
        personAddRequest.setId(generatePersonId());
        personAddRequest.setName("测试人员");
        personAddRequest.setCode("ABC123");
        personAddRequest.setPaperType(12);
        personAddRequest.setPaperNumber("hz110201");
        personAddRequest.setPaperAddress("浙江省杭州市");
        personAddRequest.setDepartmentId(1l);
        personAddRequest.setValidStartTime("2024-03-19 00:00:00");
        personAddRequest.setValidEndTime("2025-03-19 23:59:59");
        //获取RSA加密公钥
        GetPublicKeyResponse getPublicKeyResponse = getPublicKey();
        log.info("publicKey:{}",getPublicKeyResponse.getData().getPublicKey());
        log.info("key:{}",getPublicKeyResponse.getData().getKey());
        personAddRequest.setPasswordKey(getPublicKeyResponse.getData().getKey());
        //人员明文密码
        String password="355079";
        try {
            personAddRequest.setPassword(RSAUtil.encrypt(password,getPublicKeyResponse.getData().getPublicKey()));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        //设置生物特征数据（人像头像、人像特征、指纹特征）
        List<PersonAddRequest.PersonBioSignature> personBioSignatures = new ArrayList<>();
        PersonAddRequest.PersonBioSignature personBioSignature = new PersonAddRequest.PersonBioSignature();
        personBioSignature.setType(3);
        personBioSignature.setIndex(1);
        personBioSignature.setPath(uploadImageByFile("D:"+ File.separator+"image.jpg"));
        personBioSignatures.add(personBioSignature);
        personAddRequest.setPersonBiosignatures(personBioSignatures);

        //设置自定义字段
        PersonAddRequest.FieldExt fieldExt = new PersonAddRequest.FieldExt();
        fieldExt.setBusinessType("4");
        Map<String,String> useFieldNames = new HashMap<>();
        useFieldNames.put("gse87icogrnlqy3utchz","244031");
        fieldExt.setUseFieldNames(useFieldNames);
        personAddRequest.setFieldExt(fieldExt);

        Long id=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        PersonAddResponse response=null;
        try {
            log.info("PersonDemo,addPerson,request:{}", JSONUtil.toJsonStr(personAddRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/person/subsystem/add", personAddRequest,null, Method.POST , config, PersonAddResponse.class);
            log.info("PersonDemo,addPerson,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("人员添加失败:{}",response.getErrMsg());
        }else {
            id = response.getData().getId();
            log.info("人员添加成功:{}",id);
        }
        return id;
    }

    /**
     * 更新人员
     * @return 人员id
     */
    public Long updatePerson(){
        PersonUpdateRequest personUpdateRequest=new PersonUpdateRequest();
        personUpdateRequest.setId(733575L);
        personUpdateRequest.setName("测试人员修改");
        personUpdateRequest.setCode("ABC123");
        personUpdateRequest.setPaperType(12);
        personUpdateRequest.setPaperNumber("hz110201");
        personUpdateRequest.setPaperAddress("浙江省杭州市");
        personUpdateRequest.setDepartmentId(1L);
        personUpdateRequest.setValidStartTime("2024-03-19 00:00:00");
        personUpdateRequest.setValidEndTime("2025-03-26 23:59:59");
        //获取RSA加密公钥
        GetPublicKeyResponse getPublicKeyResponse = getPublicKey();
        log.info("publicKey:{}",getPublicKeyResponse.getData().getPublicKey());
        log.info("key:{}",getPublicKeyResponse.getData().getKey());
        personUpdateRequest.setPasswordKey(getPublicKeyResponse.getData().getKey());
        //人员明文密码
        String password="355000";
        try {
            personUpdateRequest.setPassword(RSAUtil.encrypt(password,getPublicKeyResponse.getData().getPublicKey()));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        //设置生物特征数据（人像头像、人像特征、指纹特征）
        List<PersonUpdateRequest.PersonBioSignature> personBioSignatures = new ArrayList<>();
        PersonUpdateRequest.PersonBioSignature personBioSignature = new PersonUpdateRequest.PersonBioSignature();
        personBioSignature.setType(3);
        personBioSignature.setIndex(1);
        personBioSignature.setPath(uploadImageByFile("D:"+ File.separator+"image.jpg"));
        personBioSignatures.add(personBioSignature);
        personUpdateRequest.setPersonBiosignatures(personBioSignatures);

        //设置自定义字段
        PersonUpdateRequest.FieldExt fieldExt = new PersonUpdateRequest.FieldExt();
        fieldExt.setBusinessType("4");
        Map<String,String> useFieldNames = new HashMap<>();
        useFieldNames.put("gse87icogrnlqy3utchz","244031");
        fieldExt.setUseFieldNames(useFieldNames);
        personUpdateRequest.setFieldExt(fieldExt);

        Long id=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        PersonAddResponse response=null;
        try {
            log.info("PersonDemo,updatePerson,request:{}", JSONUtil.toJsonStr(personUpdateRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/person/subsystem/update", personUpdateRequest,null, Method.PUT , config, PersonAddResponse.class);
            log.info("PersonDemo,updatePerson,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("人员更新失败:{}",response.getErrMsg());
        }else {
            id = response.getData().getId();
            log.info("人员更新成功:{}",id);
        }
        return id;
    }

    /**
     * 人员信息分页查询（推荐使用）
     * @param personPageRequest
     * @return
     */
    public PersonPageResponse getPersonPage(PersonPageRequest personPageRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        PersonPageResponse response=null;
        try {
            log.info("PersonDemo,getPersonPage,request:{}", JSONUtil.toJsonStr(personPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/person/subsystem/page", personPageRequest,null, Method.POST , config, PersonPageResponse.class);
            log.info("PersonDemo,getPersonPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("人员信息分页查询失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 获取全量人员信息（不推荐使用）
     * @return
     */
    public void getPersonList(){
        PersonPageRequest personPageRequest=new PersonPageRequest();
        personPageRequest.setPageNum(1);
        personPageRequest.setPageSize(50);
        List<Integer> statusList = new ArrayList<>();
        //获取状态为正常的人员
        statusList.add(1);
        personPageRequest.setStatusList(statusList);
        PersonPageResponse personPageResponse = getPersonPage(personPageRequest);
        if(personPageResponse.isSuccess()){
            if(!CollectionUtils.isEmpty(personPageResponse.getData().getPageData())) {
                Integer totalPages = personPageResponse.getData().getTotalPage();
                //若总页数大于0，遍历获取所有人员信息
                if (totalPages!=null&&totalPages>0) {
                    for (int i=0;i<totalPages;i++){
                        personPageRequest.setPageNum(i+1);
                        personPageResponse = getPersonPage(personPageRequest);
                        //TODO: 进行数据存储工作或其他操作
                    }
                }
            }
        }
    }

    /**
     * 订阅人员的增删改（调一次获取人员全量信息方法后，订阅人员增删改可实现增量获取人员信息）
     */
    public void subscribePersonOperation(){
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        SubscribeRequest.Param param = new SubscribeRequest.Param();

        SubscribeRequest.Param.Subsystem subsystem = new SubscribeRequest.Param.Subsystem();
        subsystem.setName("10.54.20.33_8003");
        subsystem.setMagic("10.54.20.33_8003");
        param.setSubsystem(subsystem);

        List<SubscribeRequest.Param.Monitor> monitors = new ArrayList();
        SubscribeRequest.Param.Monitor monitor = new SubscribeRequest.Param.Monitor();
        //回调地址的接口代码可参考SubscribeCallBackController类中的receiveMsg方法，该地址由三方提供
        monitor.setMonitor("http://10.54.20.33:8003/receiveMsg");

        List<SubscribeRequest.Param.Monitor.Event>events = new ArrayList<>();
        SubscribeRequest.Param.Monitor.Event event = new SubscribeRequest.Param.Monitor.Event();
        //订阅业务事件，填business
        event.setCategory("business");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定业务类型可参考下列代码进行修改，若订阅所有报警类型，则把下列types相关代码删除即可
        List<String> types= new ArrayList<>();
        types.add("person.add");
        types.add("person.update");
        types.add("person.delete");
        types.add("person.batch_add");
        types.add("person.batch_update");
        types.add("person.department_update");
        authority.setTypes(types);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("PersonDemo,subscribePersonOperation,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("PersonDemo,subscribePersonOperation,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

}
