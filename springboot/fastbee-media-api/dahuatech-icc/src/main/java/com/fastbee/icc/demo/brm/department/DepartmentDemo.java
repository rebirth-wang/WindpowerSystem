package com.fastbee.icc.demo.brm.department;

import java.util.HashMap;
import java.util.Map;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.brm.department.*;

/**
 * @className DepartmentDemo
 * @Author 355079
 * @Date 2025/3/21
 * @Description 基础资源部门相关接口调用demo演示
 */
@Slf4j
@Data
public class DepartmentDemo {
    /**
     * 添加部门
     * @param departmentAddRequest
     * @return
     */
    public Long addDepartment(DepartmentAddRequest departmentAddRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DepartmentAddResponse response = null;
        Long id = null;
        try {
            log.info("DepartmentDemo,addDepartment,request:{}", JSONUtil.toJsonStr(departmentAddRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/department/add", departmentAddRequest, null, Method.POST, config, DepartmentAddResponse.class);
            log.info("DepartmentDemo,addDepartment,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("部门添加失败:{}", response.getErrMsg());
        } else {
            id = response.getData().getId();
            log.info("部门添加成功:{}", id);
        }
        return id;
    }

    /**
     * 更新部门
     * @param departmentUpdateRequest
     * @return
     */
    public Long updateDepartment(DepartmentUpdateRequest departmentUpdateRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DepartmentUpdateResponse response = null;
        Long id = null;
        try {
            log.info("DepartmentDemo,updateDepartment,request:{}", JSONUtil.toJsonStr(departmentUpdateRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.0.0/department/update", departmentUpdateRequest, null, Method.PUT, config, DepartmentUpdateResponse.class);
            log.info("DepartmentDemo,updateDepartment,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("部门更新失败:{}", response.getErrMsg());
        } else {
            id = response.getData().getId();
            log.info("部门更新成功:{}", id);
        }
        return id;
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    public void deleteDepartment(Long id) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DepartmentUpdateResponse response = null;
        Map<String,Long> idMap = new HashMap<>();
        try {
            idMap.put("id",id);
            log.info("DepartmentDemo,deleteDepartment,request:{}", JSONUtil.toJsonStr(idMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.0.0/department/delete", idMap, null, Method.DELETE, config, DepartmentUpdateResponse.class);
            log.info("DepartmentDemo,deleteDepartment,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("部门删除失败:{}", response.getErrMsg());
        } else {
            log.info("部门删除成功");
        }
    }


    /**
     * 部门分页查询
     * @param departmentPageRequest
     * @return
     */
    public DepartmentPageResponse getDepartmentPage(DepartmentPageRequest departmentPageRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        DepartmentPageResponse response = null;
        try {
            log.info("DepartmentDemo,getDepartmentPage,request:{}", JSONUtil.toJsonStr(departmentPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-brm/1.2.0/department/page", departmentPageRequest, null, Method.POST, config, DepartmentPageResponse.class);
            log.info("DepartmentDemo,getDepartmentPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("部门分页查询失败:{}", response.getErrMsg());
        }
        return response;
    }

}
