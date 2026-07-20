package com.fastbee.icc.model.brm.department;

import lombok.Data;

/**
 * @className DepartmentAddRequest
 * @Author 355079
 * @Date 2025/3/21
 * @Description 部门添加请求参数
 */
@Data
public class DepartmentUpdateRequest {
    /** 部门Id */
    private Long id;
    /** 部门名称 */
    private String name;
    /** 部门类型 */
    private String depType;
    /** 部门编码 */
    private String departmentCode;
    /** 部门唯一标识码 */
    private String departmentSn;
    /** 部门负责人人员编码 */
    private String thoseResponsible;
    /** 部门联系电话 */
    private String phone;
    /** 部门邮箱 */
    private String email;
    /** 部门地址, */
    private String address;
    /** 备注 */
    private String memo;
}
