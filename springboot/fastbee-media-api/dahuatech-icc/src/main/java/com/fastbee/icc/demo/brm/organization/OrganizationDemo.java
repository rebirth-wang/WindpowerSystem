package com.fastbee.icc.demo.brm.organization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.brm.organization.*;

/**
 * @className OrganizationDemo
 * @Author 355079
 * @Date 2025/3/4
 * @Description 基础资源组织相关接口调用demo演示
 */
@Slf4j
@Data
public class OrganizationDemo {

    /**
     * 添加组织
     * @param organizationAddRequest
     * @return
     */
    public String addOrganization(OrganizationAddRequest organizationAddRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        OrganizationAddResponse response = null;
        String orgCode = null;
        try {
            log.info("OrganizationDemo,addOrganization,request:{}", JSONUtil.toJsonStr(organizationAddRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/organization/add", organizationAddRequest, null, Method.POST, config, OrganizationAddResponse.class);
            log.info("OrganizationDemo,addOrganization,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("组织添加失败:{}", response.getErrMsg());
        } else {
            orgCode = response.getData().getOrgCode();
            log.info("组织添加成功:{}", orgCode);
        }
        return orgCode;
    }

    /**
     * 添加组织
     * @param organizationUpdateRequest
     * @return
     */
    public String updateOrganization(OrganizationUpdateRequest organizationUpdateRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        OrganizationUpdateResponse response = null;
        String orgCode = null;
        try {
            log.info("OrganizationDemo,updateOrganization,request:{}", JSONUtil.toJsonStr(organizationUpdateRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/organization/update", organizationUpdateRequest, null, Method.PUT, config, OrganizationUpdateResponse.class);
            log.info("OrganizationDemo,updateOrganization,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("组织更新失败:{}", response.getErrMsg());
        } else {
            orgCode = response.getData().getOrgCode();
            log.info("组织更新成功:{}", orgCode);
        }
        return orgCode;
    }

    /**
     * 删除组织
     * @param orgCodes
     * @return
     */
    public void deleteOrganization(List<String> orgCodes) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        OrganizationUpdateResponse response = null;
        Map<String,List<String>> idMap = new HashMap<>();
        try {
            idMap.put("orgCodes",orgCodes);
            log.info("OrganizationDemo,deleteOrganization,request:{}", JSONUtil.toJsonStr(idMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/organization/delete", idMap, null, Method.DELETE, config, OrganizationUpdateResponse.class);
            log.info("OrganizationDemo,deleteOrganization,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("组织删除失败:{}", response.getErrMsg());
        } else {
            log.info("组织删除成功");
        }
    }

    /**
     * 组织分页查询
     * @param organizationPageRequest
     * @return
     */
    public OrganizationPageResponse getOrganizationPage(OrganizationPageRequest organizationPageRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        OrganizationPageResponse response = null;
        try {
            log.info("OrganizationDemo,getOrganizationPage,request:{}", JSONUtil.toJsonStr(organizationPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/organization/subsystem/page", organizationPageRequest, null, Method.POST, config, OrganizationPageResponse.class);
            log.info("OrganizationDemo,getOrganizationPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("组织分页查询失败:{}", response.getErrMsg());
        }
        return response;
    }
}
