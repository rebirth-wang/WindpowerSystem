package com.fastbee.icc.demo.face.faceSearch;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.face.faceSearch.*;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-15 11:27
 * @Description: 以图搜图
 */
@Slf4j
public class FaceSearchDemo {
    protected  String host = "124.160.33.135";
    protected  String port = "4077";
    protected  String username = "TEST";
    protected  String password = "OGR28u6_cc";
    protected  String clientId = "CompanyName";
    protected  String clientSecret = "42bec152-8f04-476a-9aec-e7d616ff3cb3";
    protected  boolean isHttp = false;

    /**
     * 以图搜图前置条件（查询设备树）
     * @param getDeviceTreeRequest
     * @return
     */
    public GetDeviceTreeResponse getDeviceTree(GetDeviceTreeRequest getDeviceTreeRequest){
        GetDeviceTreeResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/evo-face/tree/getDevChnIdsAndName";
            url +=getDeviceTreeRequest.getUrlSuffix();
            log.info("FaceSearchDemo,getDeviceTree,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, GetDeviceTreeResponse.class);
            log.info("FaceSearchDemo,getDeviceTree,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("查询设备树失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 抓拍库以图搜图前置条件（根据设备编码获取通道列表）
     * @param getChannelCodeListRequest
     * @return
     */
    public GetChannelCodeListResponse getChannelCodeList(GetChannelCodeListRequest getChannelCodeListRequest){
        GetChannelCodeListResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,getChannelCodeList,request:{}", JSONUtil.toJsonStr(getChannelCodeListRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-face/faceSearch/channelList", getChannelCodeListRequest,null, Method.POST,config, GetChannelCodeListResponse.class);
            log.info("FaceSearchDemo,getChannelCodeList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("根据设备编码查询通道列表失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 人像库搜图前置条件（根据设备编码查询人像库）
     * @param getGroupRequest
     * @return
     */
    public GetGroupResponse getGroup(GetGroupRequest getGroupRequest){
        GetGroupResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,getGroup,request:{}", JSONUtil.toJsonStr(getGroupRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-face/faceSearch/getGroupRelByCondition", getGroupRequest,null, Method.POST,config, GetGroupResponse.class);
            log.info("FaceSearchDemo,getGroup,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("根据设备编码查询人像库失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 人像以图搜图创建搜索任务
     * @param createSearchTaskRequest
     * @return
     */
    public CreateSearchTaskResponse createSearchTask(CreateSearchTaskRequest createSearchTaskRequest){
        CreateSearchTaskResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,createSearchTask,request:{}", JSONUtil.toJsonStr(createSearchTaskRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-face/1.1.0/faceSearch/search", createSearchTaskRequest,null, Method.POST,config, CreateSearchTaskResponse.class);
            log.info("FaceSearchDemo,createSearchTask,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("人像以图搜图创建搜索任务失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 以图搜图任务进度查询
     * @param sessionid
     * @return
     */
    public QueryTaskProgressResponse queryTaskProgress(String sessionid){
        QueryTaskProgressResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,queryTaskProgress,request:sessionid={}", sessionid);
            String url = "/evo-apigw/evo-face/faceSearch/third/queryProgress/"+sessionid;
            response = HttpUtils.executeJson(url, null,null, Method.GET,config, QueryTaskProgressResponse.class);
            log.info("FaceSearchDemo,queryTaskProgress,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("以图搜图任务进度查询失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 根据任务获取搜索结果
     * @param getTaskResultRequest
     * @return
     */
    public GetTaskResultResponse getTaskResult(GetTaskResultRequest getTaskResultRequest){
        GetTaskResultResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,getTaskResult,request:{}", JSONUtil.toJsonStr(getTaskResultRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-face/faceSearch/third/page", getTaskResultRequest,null, Method.POST , config, GetTaskResultResponse.class);
            log.info("FaceSearchDemo,getTaskResult,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("根据任务获取搜索结果失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 以图搜图-同步
     * @param faceSearchSyncRequest
     * @return
     */
    public FaceSearchSyncResponse faceSearchSync(FaceSearchSyncRequest faceSearchSyncRequest){
        FaceSearchSyncResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("FaceSearchDemo,faceSearchSync,request:{}", JSONUtil.toJsonStr(faceSearchSyncRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-face/faceSearch/third/faceSearchSync", faceSearchSyncRequest,null, Method.POST , config, FaceSearchSyncResponse.class);
            log.info("FaceSearchDemo,faceSearchSync,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("查询同步以图搜图信息失败：{}",response.getErrMsg());
        }
        return response;
    }


}
