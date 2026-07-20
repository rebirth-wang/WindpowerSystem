package com.fastbee.icc.demo.brm.organization;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import com.fastbee.icc.model.brm.organization.OrganizationAddRequest;
import com.fastbee.icc.model.brm.organization.OrganizationPageRequest;
import com.fastbee.icc.model.brm.organization.OrganizationUpdateRequest;

/**
 * @className OrganizationDemoTest
 * @Author 355079
 * @Date 2025/3/4
 * @Description 基础资源组织相关接口调用测试
 */
@Slf4j
@Data
public class OrganizationDemoTest {

    private OrganizationDemo organizationDemo;

    public OrganizationDemoTest() {
        organizationDemo = new OrganizationDemo();
    }

    /**
     * 测试添加组织
     */
    @Test
    public void testAddOrganization() {
        OrganizationAddRequest organizationAddRequest = new OrganizationAddRequest();
        organizationAddRequest.setOrgName("测试组织添加");
        organizationAddRequest.setOrgPreCode("001");
        organizationAddRequest.setOrgSn("15345w6334323152");
        organizationAddRequest.setOrgType("org_base");
        organizationAddRequest.setProvince("330000");
        organizationAddRequest.setCity("330100");
        organizationAddRequest.setForbidBindUser(1);
        organizationAddRequest.setService("evo-thirdParty");
        organizationDemo.addOrganization(organizationAddRequest);

    }

    /**
     * 测试删除组织
     */
    @Test
    public void testDeleteOrganization() {
        List<String> orgCodes = new ArrayList<>();
        orgCodes.add("001176");
        organizationDemo.deleteOrganization(orgCodes);

    }

    /**
     * 测试更新组织
     */
    @Test
    public void testUpdateOrganization() {
        OrganizationUpdateRequest organizationUpdateRequest = new OrganizationUpdateRequest();
        organizationUpdateRequest.setOrgName("测试组织添加2");
        organizationUpdateRequest.setOrgCode("001176");
        organizationUpdateRequest.setOrgSn("15345w6334323152");
        organizationUpdateRequest.setOrgType("org_base");
        organizationUpdateRequest.setProvince("330000");
        organizationUpdateRequest.setCity("330100");
        organizationUpdateRequest.setForbidBindUser(1);
        organizationUpdateRequest.setService("evo-thirdParty");
        organizationDemo.updateOrganization(organizationUpdateRequest);

    }

    /**
     * 测试分页查询组织信息
     */
    @Test
    public void testGetOrganizationPage() {
        OrganizationPageRequest organizationPageRequest = new OrganizationPageRequest();
        organizationPageRequest.setPageNum(1);
        organizationPageRequest.setPageSize(100);
        organizationPageRequest.setIncludeSubOrgCodeFlag(true);
        organizationDemo.getOrganizationPage(organizationPageRequest);

    }
}
