package com.fastbee.icc.demo.event.eventSubscribe;

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
import com.fastbee.icc.model.event.eventSubcribe.SubscribeListResponse;
import com.fastbee.icc.model.event.eventSubcribe.SubscribeRequest;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-11 10:51
 * @Description: 事件订阅
 */
@Slf4j
public class SubscribeDemo  {

    /**
     * 通过JSON请求体订阅事件
     *
     * @param jsonRequestBody JSON请求体
     */
    public void subscribeEventByJson(String jsonRequestBody) {
        SubscribeRequest subscribeRequest = JSONUtil.toBean(jsonRequestBody, SubscribeRequest.class);
        log.info("SubscribeDemo,subscribeEventByJson,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("SubscribeDemo,subscribeEventByJson,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 订阅报警事件
     */
    public void subscribeAlarmEvent(){
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
        types.add("57");
        types.add("51");
        types.add("61");
        authority.setTypes(types);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
        List<String>orgs = new ArrayList<>();
        orgs.add("001001");
        orgs.add("001002");
        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
        List<String>nodeCodes=new ArrayList();
        nodeCodes.add("1002511$7$0$0");
        nodeCodes.add("1002509$7$0$0");
        authority.setNodeCodes(nodeCodes);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("SubscribeDemo,subscribeAlarmEvent,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("SubscribeDemo,subscribeAlarmEvent,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 订阅业务事件
     */
    public void subscribeBusinessEvent(){
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
        types.add("car.capture");
        types.add("car.access");
        types.add("car.consume");
        authority.setTypes(types);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
        List<String>orgs = new ArrayList<>();
        orgs.add("001001");
        orgs.add("001002");
        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
        List<String>nodeCodes=new ArrayList();
        nodeCodes.add("1002523$14$0$0");
        nodeCodes.add("1002523$14$0$1");
        authority.setNodeCodes(nodeCodes);

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
            log.info("SubscribeDemo,subscribeBusinessEvent,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 订阅感知事件
     */
    public void subscribePerceptionEvent(){
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        SubscribeRequest.Param param = new SubscribeRequest.Param();

        SubscribeRequest.Param.Subsystem subsystem = new SubscribeRequest.Param.Subsystem();
        subsystem.setName("10.54.20.33_8003");
        subsystem.setMagic("10.54.20.33_8003");
        param.setSubsystem(subsystem);

        List<SubscribeRequest.Param.Monitor> monitors = new ArrayList();
        SubscribeRequest.Param.Monitor monitor = new SubscribeRequest.Param.Monitor();
        //回调地址的接口代码可参考SubscribeCallBackController类中的receiveMsg方法
        monitor.setMonitor("http://10.54.20.33:8003/receivenMsg");

        List<SubscribeRequest.Param.Monitor.Event>events = new ArrayList<>();
        SubscribeRequest.Param.Monitor.Event event = new SubscribeRequest.Param.Monitor.Event();
        //订阅感知事件，填perception
        event.setCategory("perception");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定感知类类型可参考下列代码进行修改，若订阅所有感知类类型，则把下列types相关代码删除即可
        List<String> types= new ArrayList<>();
        types.add("pmms.perception.msg");
        types.add("reportGPSInfo");
        authority.setTypes(types);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
        List<String>orgs = new ArrayList<>();
        orgs.add("001001");
        orgs.add("001002");
        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
        List<String>nodeCodes=new ArrayList();
        nodeCodes.add("1002524$12$0$0");
        nodeCodes.add("1002524$12$0$1");
        authority.setNodeCodes(nodeCodes);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("SubscribeDemo,subscribePerceptionEvent,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("SubscribeDemo,subscribePerceptionEvent,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 订阅状态事件
     */
    public void subscribeStateEvent(){
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
        //订阅状态事件，填state
        event.setCategory("state");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs相关代码删除
        List<String>orgs = new ArrayList<>();
        orgs.add("001001");
        orgs.add("001002");
        authority.setOrgs(orgs);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes相关代码删除
        List<String>nodeCodes=new ArrayList();
        nodeCodes.add("1002524$12$0$0");
        nodeCodes.add("1002524$12$0$1");
        authority.setNodeCodes(nodeCodes);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("SubscribeDemo,subscribeStateEvent,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("SubscribeDemo,subscribeStateEvent,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 订阅报警和业务事件
     */
    public void subscribeAlarmAndBusinessEvent(){
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

        //订阅报警事件
        SubscribeRequest.Param.Monitor.Event alarmEvent = new SubscribeRequest.Param.Monitor.Event();
        //订阅报警事件，填alarm
        alarmEvent.setCategory("alarm");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities1 = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority1=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定报警类型可参考下列代码进行修改，若订阅所有报警类型，则把下列types1相关代码删除即可
        List<String> types1= new ArrayList<>();
        types1.add("42");
        types1.add("43");
        authority1.setTypes(types1);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs1相关代码删除
        List<String>orgs1 = new ArrayList<>();
        orgs1.add("001001");
        orgs1.add("001002");
        authority1.setOrgs(orgs1);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes1相关代码删除
        List<String>nodeCodes1=new ArrayList();
        nodeCodes1.add("1002511$7$0$0");
        nodeCodes1.add("1002509$7$0$0");
        authority1.setNodeCodes(nodeCodes1);

        authorities1.add(authority1);
        alarmEvent.setAuthorities(authorities1);
        events.add(alarmEvent);

        //订阅业务事件
        SubscribeRequest.Param.Monitor.Event businessEvent = new SubscribeRequest.Param.Monitor.Event();
        //订阅业务事件，填business
        businessEvent.setCategory("business");
        List<SubscribeRequest.Param.Monitor.Event.Authority> authorities2 = new ArrayList();
        SubscribeRequest.Param.Monitor.Event.Authority authority2=new SubscribeRequest.Param.Monitor.Event.Authority();

        //若订阅指定业务类型可参考下列代码进行修改，若订阅所有报警类型，则把下列types2相关代码删除即可
        List<String> types2= new ArrayList<>();
        types2.add("cardRecord.offline");
        authority2.setTypes(types2);

        //若订阅指定组织可参考下列代码进行修改,若订阅所有组织，则将下列orgs2相关代码删除
        List<String>orgs2 = new ArrayList<>();
        orgs2.add("001001");
        orgs2.add("001002");
        authority2.setOrgs(orgs2);

        //若订阅指定设备和通道可参考下列代码进行修改,若订阅所有设备和通道，则将下列nodeCodes2相关代码删除
        List<String>nodeCodes2=new ArrayList();
        nodeCodes2.add("1002511$7$0$0");
        nodeCodes2.add("1002509$7$0$0");
        authority2.setNodeCodes(nodeCodes2);

        authorities2.add(authority2);
        businessEvent.setAuthorities(authorities2);
        events.add(businessEvent);

        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("SubscribeDemo,subscribeAlarmAndBusinessEvent,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config,GeneralResponse.class);
            log.info("SubscribeDemo,subscribeAlarmAndBusinessEvent,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 取消订阅
     * @param name 订阅者名称
     */
    public void cancelSubscribe(String name){
        String url = "/evo-apigw/evo-event/1.0.0/subscribe/mqinfo?name="+name;
        log.info("SubscribeDemo,cancelSubscribe,url:{}", JSONUtil.toJsonStr(url));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson(url, null,null, Method.DELETE, config, GeneralResponse.class);
            log.info("SubscribeDemo,cancelSubscribe,response:{}", response);
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 事件订阅列表查询
     * @param category 事件大类
     * @return
     */
    public SubscribeListResponse getSubscribeList(String category){
        SubscribeListResponse response=null;
        String url = "/evo-apigw/evo-event/1.0.0/subscribe/subscribe-list?monitorType=url&category="+category;
        log.info("SubscribeDemo,getSubscribeList,url:{}", JSONUtil.toJsonStr(url));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            response = HttpUtils.executeJson(url, null,null, Method.GET, config,SubscribeListResponse.class);
            log.info("SubscribeDemo,getSubscribeList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        return response;
    }


}
