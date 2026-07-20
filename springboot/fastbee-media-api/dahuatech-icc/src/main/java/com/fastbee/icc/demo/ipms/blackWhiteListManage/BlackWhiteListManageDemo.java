package com.fastbee.icc.demo.ipms.blackWhiteListManage;

import java.util.List;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.ipms.blackWhiteListManage.*;

/**
 * @className blacklistManageDemo
 * @Author 355079
 * @Date 2024/12/9
 * @Description 黑白名单管理
 */
@Slf4j
public class BlackWhiteListManageDemo {

    /**
     * 下发设备白名单
     *
     * @param updateWhiteListRequest 下发白名单请求列表
     * @return 通用响应
     */
    public GeneralResponse updateWhiteList(List<UpdateWhiteListParam> updateWhiteListRequest) {
        GeneralResponse response = null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carWhiteList/update";
            log.info("BlackWhiteListManageDemo,updateWhiteList,request:{}", (JSONUtil.parseArray(updateWhiteListRequest)));
            response = HttpUtils.executeJson(url, JSONUtil.parseArray(updateWhiteListRequest), null, Method.POST, config, GeneralResponse.class);
            log.info("BlackWhiteListManageDemo,updateWhiteList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("下发设备白名单失败：{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 查询设备白名单任务状态
     * @param queryTaskStatusRequest
     * @return
     */
    public QueryTaskStatusResponse queryTaskStatus(QueryTaskStatusRequest queryTaskStatusRequest) {
        QueryTaskStatusResponse response = null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carWhiteListTask/list" + queryTaskStatusRequest.getUrlSuffix();
            log.info("BlackWhiteListManageDemo,queryTaskStatus,url:{}", url);
            response = HttpUtils.executeJson(url, null, null, Method.GET, config, QueryTaskStatusResponse.class);
            log.info("BlackWhiteListManageDemo,queryTaskStatus,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("查询设备白名单任务状态失败：{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 查询白名单信息
     *
     * @param queryWhiteListInfoRequest 查询白名单信息请求
     * @return 查询白名单信息返回结果
     */
    public QueryWhiteListInfoResponse queryWhiteListInfo(QueryWhiteListInfoRequest queryWhiteListInfoRequest) {
        QueryWhiteListInfoResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carWhiteList/sendCars"+queryWhiteListInfoRequest.getUrlSuffix();
            log.info("BlackWhiteListManageDemo,queryWhiteListInfo,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, QueryWhiteListInfoResponse.class);
            log.info("BlackWhiteListManageDemo,queryWhiteListInfo,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("查询白名单信息失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 从所有设备白名单中删除指定车辆
     * @param carIds
     * @return
     */
    public GeneralResponse deleteWhiteList(List<String> carIds) {
        GeneralResponse response = null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carWhiteList/delete/batch";
            log.info("BlackWhiteListManageDemo,deleteWhiteList,request:{}", (JSONUtil.parseArray(carIds)));
            response = HttpUtils.executeJson(url, JSONUtil.parseArray(carIds), null, Method.POST, config, GeneralResponse.class);
            log.info("BlackWhiteListManageDemo,deleteWhiteList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("删除指定车辆设备白名单失败：{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 下发设备黑名单名单
     * @param updateBlackListRequest 下发黑名单请求列表
     * @return 通用响应
     */
    public GeneralResponse updateBlackList(List<UpdateBlackListParam> updateBlackListRequest) {
        GeneralResponse response = null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carBlackList/update";
            log.info("BlackBlackListManageDemo,updateBlackList,request:{}", (JSONUtil.parseArray(updateBlackListRequest)));
            response = HttpUtils.executeJson(url, JSONUtil.parseArray(updateBlackListRequest), null, Method.POST, config, GeneralResponse.class);
            log.info("BlackBlackListManageDemo,updateBlackList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("下发设备黑名单失败：{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 查询黑名单信息
     * @param queryBlackListInfoRequest 查询黑名单信息请求
     * @return 查询黑名单信息返回结果
     */
    public QueryBlackListInfoResponse queryBlackListInfo(QueryBlackListInfoRequest queryBlackListInfoRequest) {
        QueryBlackListInfoResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carBlackList/sendCars"+queryBlackListInfoRequest.getUrlSuffix();
            log.info("BlackBlackListManageDemo,queryBlackListInfo,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, QueryBlackListInfoResponse.class);
            log.info("BlackBlackListManageDemo,queryBlackListInfo,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("查询黑名单信息失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 从所有设备黑名单中删除指定车辆
     * @param carIds
     * @return
     */
    public GeneralResponse deleteBlackList(List<String> carIds) {
        GeneralResponse response = null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/carBlackList/delete/batch";
            log.info("BlackBlackListManageDemo,deleteBlackList,request:{}", (JSONUtil.parseArray(carIds)));
            response = HttpUtils.executeJson(url, JSONUtil.parseArray(carIds), null, Method.POST, config, GeneralResponse.class);
            log.info("BlackBlackListManageDemo,deleteBlackList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("删除指定车辆设备黑名单失败：{}", response.getErrMsg());
        }
        return response;
    }

}
