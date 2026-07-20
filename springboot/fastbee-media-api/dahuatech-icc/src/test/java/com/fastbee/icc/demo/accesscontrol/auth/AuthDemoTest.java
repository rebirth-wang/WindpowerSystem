package com.fastbee.icc.demo.accesscontrol.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fastbee.icc.model.accesscontrol.auth.DepartmentAuthRequest;
import com.fastbee.icc.model.accesscontrol.auth.PersonAuthRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 14:11
 * @Description: 门禁授权测试
 */
public class AuthDemoTest {
    private AuthDemo authDemo;

    public AuthDemoTest() {
        authDemo = new AuthDemo();
    }

    /**
     * 测试按人新增授权
     */
    @Test
    public void testPersonAuth(){
        PersonAuthRequest personAuthRequest = new PersonAuthRequest();
        List<String> personCodes = new ArrayList<>();
        personCodes.add("2440317578");
        personAuthRequest.setPersonCodes(personCodes);
        personAuthRequest.setTimeQuantumId(1l);
        List<PersonAuthRequest.PrivilegeDetail> privilegeDetails = new ArrayList<>();
        //按通道授权
        PersonAuthRequest.PrivilegeDetail privilegeDetail1 = new PersonAuthRequest.PrivilegeDetail();
        privilegeDetail1.setPrivilegeType(1);
        privilegeDetail1.setResourceCode("1002600$7$0$0");
        privilegeDetail1.setTimeQuantumId("1");
        privilegeDetail1.setStartDate("2024-04-22 00:00:00");
        privilegeDetail1.setEndDate("2024-04-22 23:59:59");
        privilegeDetails.add(privilegeDetail1);
        personAuthRequest.setPrivilegeDetails(privilegeDetails);
        authDemo.personAuth(personAuthRequest);
    }

    /**
     * 测试按部门新增授权
     */
    @Test
    public void testDeptAuth(){
        DepartmentAuthRequest departmentAuthRequest = new DepartmentAuthRequest();
        departmentAuthRequest.setDeptIds(Arrays.asList(1012602l));
        departmentAuthRequest.setChannelCodes(Arrays.asList("1002600$7$0$0"));
        departmentAuthRequest.setTimeQuantumId(1l);
        authDemo.deptAuth(departmentAuthRequest);
    }
}
