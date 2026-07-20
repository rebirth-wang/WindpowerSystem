package com.fastbee.icc.demo.video.remoteCapture;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.video.remoteCapture.OperationVO;
import com.fastbee.icc.model.video.remoteCapture.RemoteCaptureRequest;
import com.fastbee.icc.model.video.remoteCapture.RemoteCaptureResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-12 10:40
 * @Description: 设备远程抓图
 */
@Slf4j
public class RemoteCaptureDemo {

    /**
     * 远程设备抓图
     * @param id 消息序号，自定义但需要保证唯一
     * @param channelId 视频通道编号
     * @return
     */
    public RemoteCaptureResponse remoteCapture(Long id,String channelId){
        RemoteCaptureRequest remoteCaptureRequest = operateRemoteCaptureRequest(id,channelId);
        RemoteCaptureResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("RemoteCaptureDemo,remoteCapture,request:{}", JSONUtil.toJsonStr(remoteCaptureRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/EVO/invoke/DMS", remoteCaptureRequest,null, Method.POST , config, RemoteCaptureResponse.class);
            log.info("RemoteCaptureDemo,remoteCapture,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("远程设备抓图失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 对抓拍请求参数进行封装
     * @param id
     * @param channelId
     * @return
     */
    public RemoteCaptureRequest operateRemoteCaptureRequest(Long id,String channelId){
        String deviceCode=null,DevChannel;
        OperationVO operationVO = new OperationVO();
        operationVO.setId(id);
        OperationVO.DeviceVO params = new OperationVO.DeviceVO();
        if(!StringUtils.isEmpty(channelId)){
            deviceCode = channelId.substring(0,channelId.indexOf("$"));
            params.setDevID(deviceCode);
            DevChannel = channelId.substring(channelId.lastIndexOf("$")+1);
            params.setDevChannel(DevChannel);
        }
        operationVO.setParams(params);
        RemoteCaptureRequest remoteCaptureRequest = new RemoteCaptureRequest();
        remoteCaptureRequest.setDeviceCode(deviceCode);
        remoteCaptureRequest.setParams(JSONUtil.toJsonStr(operationVO));
        return remoteCaptureRequest;
    }

}
