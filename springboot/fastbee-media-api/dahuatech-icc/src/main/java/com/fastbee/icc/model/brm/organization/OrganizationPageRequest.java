package com.fastbee.icc.model.brm.organization;

import java.util.List;

import lombok.Data;

/**
 * @className OrganizationPageRequest
 * @Author 355079
 * @Date 2025/3/13
 * @Description 组织分页查询请求参数
 */
@Data
public class OrganizationPageRequest {
    /** 当前页 */
    private Integer pageNum;
    /** 每页记录数 */
    private Integer pageSize;
    /** 排序字段 */
    private String sort;
    /** 排序规则 */
    private String sortType;
    /** 组织类型列表 */
    private List<String> orgTypeList;
    /** 组织编码列表 */
    private List<String> orgCodeList;
    /** 组织唯一标识码列表 */
    private List<String> orgSnList;
    /** 父级组织编码列表 */
    private String preOrgCode;
    /** 是否包含子节点组织 */
    private Boolean includeSubOrgCodeFlag;

}
