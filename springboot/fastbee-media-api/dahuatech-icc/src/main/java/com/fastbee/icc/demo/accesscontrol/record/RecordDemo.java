package com.fastbee.icc.demo.accesscontrol.record;

import java.util.ArrayList;
import java.util.List;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.enums.OpenDoorTypeEnum;
import com.fastbee.icc.model.accesscontrol.record.QueryRecordCountResponse;
import com.fastbee.icc.model.accesscontrol.record.QueryRecordRequest;
import com.fastbee.icc.model.accesscontrol.record.QueryRecordResponse;
import com.fastbee.icc.model.event.eventSubcribe.SubscribeRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-26 14:48
 * @Description: 门禁事件
 */
@Slf4j
public class RecordDemo {

    /**
     * 门禁记录数量查询
     */
    public Integer getRecordCount(QueryRecordRequest queryRecordRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        QueryRecordCountResponse response=null;
        try {
            log.info("RecordDemo,getRecordCount,request:{}", JSONUtil.toJsonStr(queryRecordRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/swingCardRecord/bycondition/combinedCount", queryRecordRequest,null, Method.POST , config, QueryRecordCountResponse.class);
            log.info("RecordDemo,getRecordCount,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("门禁记录数量查询失败:{}",response.getErrMsg());
        }
        Integer num=response.getData();
        log.info("门禁记录数量:{}",num);
        return num;
    }

    /**
     * 门禁记录分页查询
     * @param queryRecordRequest
     * @return
     */
    public QueryRecordResponse getRecordPage(QueryRecordRequest queryRecordRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        QueryRecordResponse response=null;
        try {
            log.info("DeviceDemo,getRecordPage,request:{}", JSONUtil.toJsonStr(queryRecordRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/swingCardRecord/bycondition/combined", queryRecordRequest,null, Method.POST , config, QueryRecordResponse.class);
            log.info("DeviceDemo,getRecordPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("门禁记录分页查询失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 获取指定时间内所有门禁记录
     * @param queryRecordRequest
     */
    public void getRecordList(QueryRecordRequest queryRecordRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        QueryRecordResponse response=null;
        //获取门禁记录数量
        Integer totalRows = getRecordCount(queryRecordRequest);
        //获取总页数
        Integer totalPage = (totalRows-1)/queryRecordRequest.getPageSize()+1;
        log.info("RecordDemo,subscribeDeviceOperation,:totalRows{},totalPage:{}",totalRows,totalPage);
        for(int i=1;i<=totalPage;i++){
            //获取每一页门禁记录
            queryRecordRequest.setPageNum(i);
            response = getRecordPage(queryRecordRequest);
            //TODO: 进行数据存储工作或其他操作
        }
    }

    /**
     * 订阅门禁记录和补采记录（可实现增量获取门禁记录）
     */
    public void subscribeAccessControlRecord() {
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        SubscribeRequest.Param param = new SubscribeRequest.Param();

        SubscribeRequest.Param.Subsystem subsystem = new SubscribeRequest.Param.Subsystem();
        subsystem.setName("10.54.20.33_8003");
        subsystem.setMagic("10.54.20.33_8003");
        param.setSubsystem(subsystem);

        List<SubscribeRequest.Param.Monitor> monitors = new ArrayList();
        SubscribeRequest.Param.Monitor monitor = new SubscribeRequest.Param.Monitor();
        //回调地址的接口代码可参考SubscribeCallBackController类中的receiveMsg方法
        monitor.setMonitor("http://10.54.20.33:8003/receiveMsg");
        List<SubscribeRequest.Param.Monitor.Event> events = new ArrayList<>();

        //订阅门禁记录相关报警
        SubscribeRequest.Param.Monitor.Event alarmEvent = new SubscribeRequest.Param.Monitor.Event();
        alarmEvent.setCategory("alarm");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities1 = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority1 = new SubscribeRequest.Param.Monitor.Event.Authority();

        authority1.setTypes(OpenDoorTypeEnum.getOpenDoorTypes());
        authorities1.add(authority1);
        alarmEvent.setAuthorities(authorities1);
        events.add(alarmEvent);

        //订阅离线补采
        SubscribeRequest.Param.Monitor.Event businessEvent = new SubscribeRequest.Param.Monitor.Event();
        businessEvent.setCategory("business");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities2 = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority2 = new SubscribeRequest.Param.Monitor.Event.Authority();

        List<String> types2 = new ArrayList<>();
        types2.add("cardRecord.offline");
        authority2.setTypes(types2);
        authorities2.add(authority2);
        businessEvent.setAuthorities(authorities2);
        events.add(businessEvent);

        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("RecordDemo,subscribeDeviceOperation,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest, null, Method.POST, config,GeneralResponse.class);
            log.info("RecordDemo,subscribeDeviceOperation,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }
}
