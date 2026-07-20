package com.fastbee.icc.model.brm.organization;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className OrganizationAddResponse
 * @Author 355079
 * @Date 2025/3/4
 * @Description 组织新增返回结果
 */
@Data
public class OrganizationAddResponse extends IccResponse {
    private OrganizationAddData data;
    @Data
    public class OrganizationAddData {
        /** 新增组织编码 */
        private String orgCode;
    }
}
