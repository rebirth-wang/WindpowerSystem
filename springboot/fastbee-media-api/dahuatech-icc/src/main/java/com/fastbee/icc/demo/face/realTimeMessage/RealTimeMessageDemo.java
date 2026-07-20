package com.fastbee.icc.demo.face.realTimeMessage;

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
import com.fastbee.icc.model.event.eventSubcribe.SubscribeRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-17 09:06
 * @Description: 实时消息
 */
@Slf4j
public class RealTimeMessageDemo {

    protected  String host = "124.160.33.135";
    protected  String port = "4077";
    protected  String username = "TEST";
    protected  String password = "OGR28u6_cc";
    protected  String clientId = "CompanyName";
    protected  String clientSecret = "42bec152-8f04-476a-9aec-e7d616ff3cb3";
    protected  boolean isHttp = false;

    /**
     * 订阅黑名单库报警
     */
    public void subscribeBlacklistAlarm(){
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

        List<SubscribeRequest.Param.Monitor.Event>events = new ArrayList<>();
        SubscribeRequest.Param.Monitor.Event event = new SubscribeRequest.Param.Monitor.Event();
        //订阅报警事件，填alarm
        event.setCategory("alarm");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定报警类型可参考下列代码进行修改，若订阅所有报警类型，则把下列types相关代码删除即可
        List<String> types= new ArrayList<>();
        types.add("1001001");
        authority.setTypes(types);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
//        List<String>orgs = new ArrayList<>();
//        orgs.add("001001");
//        orgs.add("001002");
//        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
//        List<String>nodeCodes=new ArrayList();
//        nodeCodes.add("1002511$7$0$0");
//        nodeCodes.add("1002509$7$0$0");
//        authority.setNodeCodes(nodeCodes);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("RealTimeMessageDemo,subscribeBlacklistAlarm,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config ,GeneralResponse.class);
            log.info("RealTimeMessageDemo,subscribeBlacklistAlarm,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }
}
