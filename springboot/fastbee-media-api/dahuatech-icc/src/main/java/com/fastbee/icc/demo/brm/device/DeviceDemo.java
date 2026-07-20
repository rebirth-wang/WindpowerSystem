package com.fastbee.icc.demo.brm.device;

import java.util.ArrayList;
import java.util.List;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.brm.device.*;
import com.fastbee.icc.model.event.eventSubcribe.SubscribeRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-26 14:48
 * @Description: 基础资源设备相关接口调用demo演示
 */
@Slf4j
public class DeviceDemo {
    private  String host = "124.160.33.135";
    private  String port = "4077";
    private  String username = "TEST";
    private  String password = "OGR28u6_cc";
    private  String clientId = "CompanyName";
    private  String clientSecret = "42bec152-8f04-476a-9aec-e7d616ff3cb3";
    private  boolean isHttp = false;

    /**
     * 分页获取设备信息
     */
    public DevicePageResponse getDevicePage(DevicePageRequest devicePageRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DevicePageResponse response=null;
        try {
            log.info("DeviceDemo,getDevicePage,request:{}", JSONUtil.toJsonStr(devicePageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/device/subsystem/page", devicePageRequest,null, Method.POST , config, DevicePageResponse.class);
            log.info("DeviceDemo,getDevicePage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("设备信息分页查询失败:{}",response.getErrMsg());
        }
        return response;
    }
    /**
     * 获取全量设备信息
     */
    public void getDeviceList(){
        DevicePageRequest devicePageRequest=new DevicePageRequest();
        devicePageRequest.setPageNum(1);
        devicePageRequest.setPageSize(50);
        DevicePageResponse devicePageResponse = getDevicePage(devicePageRequest);
        if(devicePageResponse.isSuccess()){
            if(!CollectionUtils.isEmpty(devicePageResponse.getData().getPageData())) {
                Integer totalPages = devicePageResponse.getData().getTotalPage();
                //若总页数大于0，遍历获取所有设备信息
                if (totalPages!=null&&totalPages>0) {
                    for (int i=0;i<totalPages;i++){
                        devicePageRequest.setPageNum(i+1);
                        devicePageResponse = getDevicePage(devicePageRequest);
                        //TODO: 进行数据存储工作或其他操作
                    }
                }
            }
        }
    }

    /**
     * 订阅设备的增删改（调一次获取设备全量信息方法后，订阅设备增删改可实现增量获取设备信息）
     */
    public void subscribeDeviceOperation(){
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
        types.add("device.add");
        types.add("device.update");
        types.add("device.delete");
        types.add("device.batch_add");
        types.add("device.batch_update");
        authority.setTypes(types);

        authorities.add(authority);
        event.setAuthorities(authorities);
        events.add(event);
        monitor.setEvents(events);
        monitors.add(monitor);
        param.setMonitors(monitors);
        subscribeRequest.setParam(param);
        log.info("DeviceDemo,subscribeDeviceOperation,request:{}", JSONUtil.toJsonStr(subscribeRequest));

        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            GeneralResponse response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/subscribe/mqinfo", subscribeRequest,null, Method.POST, config, GeneralResponse.class);
            log.info("DeviceDemo,subscribeDeviceOperation,response:{}", response.getResult());
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
    }

    /**
     * 分页获取通道信息
     * @param channelPageRequest
     * @return
     */
    public ChannelPageResponse getChannelPage(ChannelPageRequest channelPageRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        ChannelPageResponse response=null;
        try {
            log.info("DeviceDemo,getChannelPage,request:{}", JSONUtil.toJsonStr(channelPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/device/channel/subsystem/page", channelPageRequest,null, Method.POST , config, ChannelPageResponse.class);
            log.info("DeviceDemo,getChannelPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("设备通道分页查询失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 设备树查询
     * @param deviceTreeRequest
     * @return
     */
    public DeviceTreeResponse getDeviceTree(DeviceTreeRequest deviceTreeRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DeviceTreeResponse response=null;
        try {
            log.info("DeviceDemo,getDeviceTree,request:{}", JSONUtil.toJsonStr(deviceTreeRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.0.0/tree", deviceTreeRequest,null, Method.POST , config, DeviceTreeResponse.class);
            log.info("DeviceDemo,getDeviceTree,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("设备树查询失败:{}",response.getErrMsg());
        }
        return response;
    }
}
