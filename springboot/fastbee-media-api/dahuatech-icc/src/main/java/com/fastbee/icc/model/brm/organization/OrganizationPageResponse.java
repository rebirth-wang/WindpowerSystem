package com.fastbee.icc.model.brm.organization;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className OrganizationPageResponse
 * @Author 355079
 * @Date 2025/3/13
 * @Description 组织分页查询请求参数
 */
public class OrganizationPageResponse extends IccResponse {
    /** 分页数据 */
    private PageVO data;

    @Data
    public static class PageVO{
        /** 当前页码 */
        private Integer currentPage;
        /** 总页数 */
        private Integer totalPage;
        /** 每页条数 */
        private Integer pageSize;
        /** 送条数 */
        private Integer totalRows;
        /** 人员数据列表 */
        private List<OrgInfoVO> pageData;

        @Data
        public static class OrgInfoVO{
            /** 组织编码 */
            private String orgCode;
            /** 组织名称 */
            private String orgName;
            /** 唯一标识码 */
            private String orgSn;
            /** 组织结构类型 */
            private String orgType;
            /** 省 */
            private String province;
            /** 市 */
            private String city;
            /** 县 */
            private String county;
            /** 域ID */
            private String domainId;
            /** 状态 */
            private Integer stat;
            /** 排序码 */
            private Integer sort;
            /** 所有组织排序 */
            private String allSort;
            /** 子系统服务编码 */
            private String service;
            /** 禁止绑定用户 */
            private Integer forbidBindUser;
            /** 禁止绑定用户服务 */
            private String forbidBindService;
        }
    }
}
