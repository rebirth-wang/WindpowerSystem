package com.fastbee.icc.demo.video.ptzControl;

import java.util.List;

import com.dahuatech.hutool.core.collection.CollectionUtil;
import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.video.ptzControl.*;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-09 14:24
 * @Description: 云台控制
 */
@Slf4j
public class PtzControlDemo {

    /**
     * 云台镜头控制
     * @param operateCameraRequest
     * @return
     */
    public OperateCameraResponse operateCamera(OperateCameraRequest operateCameraRequest){
        OperateCameraResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,operateCamera,request:{}", JSONUtil.toJsonStr(operateCameraRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/OperateCamera", operateCameraRequest,null, Method.POST , config, OperateCameraResponse.class);
            log.info("PtzControlDemo,operateCamera,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("云台镜头控制失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 云台方向控制
     * @param operateDirectRequest
     * @return
     */
    public OperateDirectResponse operateDirect(OperateDirectRequest operateDirectRequest){
        OperateDirectResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,operateDirect,request:{}", JSONUtil.toJsonStr(operateDirectRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/OperateDirect", operateDirectRequest,null, Method.POST , config, OperateDirectResponse.class);
            log.info("PtzControlDemo,operateDirect,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("云台方向控制失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 云台功能控制
     * @param operateFunctionRequest
     * @return
     */
    public OperateFunctionResponse operateFunction(OperateFunctionRequest operateFunctionRequest){
        OperateFunctionResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,operateFunction,request:{}", JSONUtil.toJsonStr(operateFunctionRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/OperateFunction", operateFunctionRequest,null, Method.POST , config, OperateFunctionResponse.class);
            log.info("PtzControlDemo,operateFunction,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("云台功能控制失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 获取云台预置点
     * @param getPresetPointsRequest
     * @return
     */
    public GetPresetPointsResponse getPresetPoints(GetPresetPointsRequest getPresetPointsRequest){
        GetPresetPointsResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,getPresetPoints,request:{}", JSONUtil.toJsonStr(getPresetPointsRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/GetPresetPoints", getPresetPointsRequest,null, Method.POST , config, GetPresetPointsResponse.class);
            log.info("PtzControlDemo,getPresetPoints,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("获取云台预置点失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 预置点控制(添加/删除/定位)
     * @param operatePresetPointRequest
     * @return
     */
    public OperatePresetPointResponse operatePresetPoint(OperatePresetPointRequest operatePresetPointRequest){
        OperatePresetPointResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,operatePresetPoint,request:{}", JSONUtil.toJsonStr(operatePresetPointRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/OperatePresetPoint", operatePresetPointRequest,null, Method.POST , config, OperatePresetPointResponse.class);
            log.info("PtzControlDemo,operatePresetPoint,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("预置点控制(添加/删除/定位)失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 获取云台巡航线
     * @param getCruisesRequest
     * @return
     */
    public GetCruisesResponse getCruises(GetCruisesRequest getCruisesRequest){
        GetCruisesResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,getCruises,request:{}", JSONUtil.toJsonStr(getCruisesRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Ptz/GetCruises", getCruisesRequest,null, Method.POST , config, GetCruisesResponse.class);
            log.info("PtzControlDemo,getCruises,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("获取云台巡航线失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 保存云台巡航线
     * @param saveCruiseRequest
     * @return
     */
    public SaveCruiseResponse saveCruise(SaveCruiseRequest saveCruiseRequest){
        SaveCruiseResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            //巡航线信息转换成xml格式
            saveCruiseRequest.getData().setData(convertToXml(saveCruiseRequest.getData().getCruises()));
            log.info("PtzControlDemo,saveCruise,request:{}", JSONUtil.toJsonStr(saveCruiseRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Cruise/SaveCruise", saveCruiseRequest,null, Method.POST , config, SaveCruiseResponse.class);
            log.info("PtzControlDemo,saveCruise,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("保存云台巡航线失败：{}",response.getDesc());
        }
        return response;
    }

    /**
     * 将巡航线信息转换成xml格式
     * @param cruises
     * @return
     */
    public String convertToXml(List<CruiseInfo> cruises){
        StringBuilder cruiseConfigXml=new StringBuilder();
        cruiseConfigXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        if(CollectionUtil.isNotEmpty(cruises)) {
            cruiseConfigXml.append("<CruiseConfig>");
            for(CruiseInfo cruise:cruises){
                cruiseConfigXml.append("<Cruise id=\""+cruise.getId()+"\" name=\""+cruise.getName()+"\" pointId=\""+cruise.getPointId()+"\">");
                if(CollectionUtil.isNotEmpty(cruise.getPrePoints())){
                    for(CruiseInfo.PrePoint prePoint:cruise.getPrePoints()){
                        cruiseConfigXml.append("<PrePoint id=\""+prePoint.getId()+"\" code=\""+prePoint.getCode()+"\" stay=\""+prePoint.getStay()+"\" />");
                    }
                }
                cruiseConfigXml.append("</Cruise>");
            }
            cruiseConfigXml.append("</CruiseConfig>");
        }
        return cruiseConfigXml.toString();
    }

    /**
     * 删除云台巡航线
     * @param deleteCruiseRequest
     * @return
     */
    public DeleteCruiseResponse deleteCruise(DeleteCruiseRequest deleteCruiseRequest){
        DeleteCruiseResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PtzControlDemo,deleteCruise,request:{}", JSONUtil.toJsonStr(deleteCruiseRequest));
            response = HttpUtils.executeJson("/evo-apigw/admin/API/DMS/Cruise/DeleteCruise", deleteCruiseRequest,null, Method.POST , config, DeleteCruiseResponse.class);
            log.info("PtzControlDemo,deleteCruise,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("1000")) {
            log.info("删除云台巡航线失败：{}",response.getDesc());
        }
        return response;
    }
}
