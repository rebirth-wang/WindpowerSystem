package com.fastbee.icc.demo.brm.department;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.brm.department.DepartmentAddRequest;
import com.fastbee.icc.model.brm.department.DepartmentPageRequest;
import com.fastbee.icc.model.brm.department.DepartmentUpdateRequest;

/**
 * @className DepartmentDemoTest
 * @Author 355079
 * @Date 2025/3/21
 * @Description 基础资源部门相关接口调用测试
 */
@Slf4j
@Data
public class DepartmentDemoTest {
    private DepartmentDemo departmentDemo;

    public DepartmentDemoTest() {
        departmentDemo = new DepartmentDemo();
    }

    /**
     * 测试新增部门
     */
    @Test
    public void testAddDepartment(){
        DepartmentAddRequest departmentAddRequest = new DepartmentAddRequest();
        departmentAddRequest.setParentId(1L);
        departmentAddRequest.setName("测试部门添加");
        departmentAddRequest.setDepartmentCode("121400");
        departmentAddRequest.setDepartmentSn("121400");
        departmentDemo.addDepartment(departmentAddRequest);
    }

    /**
     * 测试更新部门
     */
    @Test
    public void testUpdateDepartment(){
        DepartmentUpdateRequest departmentUpdateRequest = new DepartmentUpdateRequest();
        departmentUpdateRequest.setId(1022766L);
        departmentUpdateRequest.setName("测试部门添加2");
        departmentDemo.updateDepartment(departmentUpdateRequest);
    }

    /**
     * 测试删除部门
     */
    @Test
    public void testDeleteDepartment(){
        Long id = 1022766L;
        departmentDemo.deleteDepartment(id);
    }

    /**
     * 测试部门分页查询
     */
    @Test
    public void testGetDepartmentPage(){
        DepartmentPageRequest departmentPageRequest = new DepartmentPageRequest();
        departmentPageRequest.setPageNum(1);
        departmentPageRequest.setPageSize(10);
        departmentDemo.getDepartmentPage(departmentPageRequest);
    }

}
