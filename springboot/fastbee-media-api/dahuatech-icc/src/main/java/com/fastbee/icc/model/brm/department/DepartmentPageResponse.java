package com.fastbee.icc.model.brm.department;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className DepartmentPageResponse
 * @Author 355079
 * @Date 2025/3/22
 * @Description 部门分页查询返回结果
 */
@Data
public class DepartmentPageResponse extends IccResponse {
    private PageVO data;
    @Data
    public static class PageVO {
        /** 当前页码 */
        private Integer currentPage;
        /** 总页数 */
        private Integer totalPage;
        /** 每页条数 */
        private Integer pageSize;
        /** 送条数 */
        private Integer totalRows;
        /** 人员数据列表 */
        private List<DeptInfoVO> pageData;

        @Data
        public static class DeptInfoVO {
            /** 部门id */
            private Long id;
            /** 上级部门id */
            private Long parentId;
            /** 部门名称 */
            private String name;
            /** 备注 */
            private String memo;
            /** 同级部门排序值 */
            private Integer sort;
            /** 部门层级关系 */
            private String parentIds;
            /** 部门唯一编码 */
            private String departmentSn;
            /** 级联节点的域id */
            private String domainId;
        }
    }
}
