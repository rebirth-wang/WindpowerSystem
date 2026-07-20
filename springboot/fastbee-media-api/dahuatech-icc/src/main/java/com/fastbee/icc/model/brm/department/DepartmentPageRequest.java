package com.fastbee.icc.model.brm.department;

import lombok.Data;

/**
 * @className DepartmentPageRequest
 * @Author 355079
 * @Date 2025/3/22
 * @Description 部门分页查询请求参数
 */
@Data
public class DepartmentPageRequest {
    /** 上级部门ID */
    private Long parentId;
    /** 是否包含所有下级节点 */
    private String containerLower;
    /** 部门id列表 */
    private String departmentIds;
    /** 部门唯一标识码列表 */
    private String departmentSns;
    /** 当前页 */
    private Integer pageNum;
    /** 每页大小 */
    private Integer pageSize;
    /** 模糊搜索关键字 */
    private String searchKey;
}
