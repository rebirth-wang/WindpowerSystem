package com.fastbee.icc.model.brm.organization;

import lombok.Data;

/**
 * @className OrganizationAddRequest
 * @Author 355079
 * @Date 2025/3/4
 * @Description 组织添加请求参数
 */
@Data
public class OrganizationAddRequest {
    /** 组织名称 */
    private String orgName;
    /** 上级组织编码 */
    private String orgPreCode;
    /** 组织唯一标识码 */
    private String orgSn;
    /** 组织类型唯一编码 */
    private String orgType;
    /** 省 */
    private String province;
    /** 市 */
    private String city;
    /** 县/小区 */
    private String county;
    /** 是否禁止关联用户 */
    private Integer forbidBindUser;
    /** 服务唯一编码(子系统服务编码),固定值evo-thirdParty */
    private String service;
}
