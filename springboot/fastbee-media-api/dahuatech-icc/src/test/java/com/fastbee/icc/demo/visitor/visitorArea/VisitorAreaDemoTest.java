package com.fastbee.icc.demo.visitor.visitorArea;

import org.junit.Test;

import com.fastbee.icc.model.visitor.visitorArea.VisitorAreaPageRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-19 09:45
 * @Description: 访问区域测试
 */
public class VisitorAreaDemoTest {
    private VisitorAreaDemo visitorAreaDemo;

    public VisitorAreaDemoTest() {
        visitorAreaDemo = new VisitorAreaDemo();
    }

    /**
     * 测试访问区域分页查询
     */
    @Test
    public void testGetVisitorAreaPage(){
        VisitorAreaPageRequest visitorAreaPageRequest = new VisitorAreaPageRequest();
        visitorAreaPageRequest.setPageNum(1);
        visitorAreaPageRequest.setPageSize(20);
        visitorAreaDemo.getVisitorAreaPage(visitorAreaPageRequest);
    }
}
