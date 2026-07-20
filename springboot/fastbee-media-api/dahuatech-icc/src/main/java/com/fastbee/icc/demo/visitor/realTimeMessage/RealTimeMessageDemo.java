package com.fastbee.icc.demo.visitor.realTimeMessage;

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
 * @Date:2024-04-19 10:42
 * @Description: 实时消息订阅
 */
@Slf4j
public class RealTimeMessageDemo {

    /**
     * 订阅访客信息状态变化和访客通行记录实时消息
     */
    public void subscribeVisitorInfo(){
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
        //订阅业务事件，填business
        event.setCategory("business");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定业务类型可参考下列代码进行修改，若订阅所有报警类型，则把下列types相关代码删除即可
        List<String> types= new ArrayList<>();
        //访客信息状态变化
        types.add("visitor.status");
        //访客行踪推送
        types.add("visitor.record");
        authority.setTypes(types);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
//        List<String>orgs = new ArrayList<>();
//        orgs.add("001001");
//        orgs.add("001002");
//        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
//        List<String>nodeCodes=new ArrayList();
//        nodeCodes.add("1002523$14$0$0");
//        nodeCodes.add("1002523$14$0$1");
//        authority.setNodeCodes(nodeCodes);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("SubscribeDemo,subscribeBusinessEvent,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("RealTimeMessageDemo,subscribeVisitorInfo,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }
}
