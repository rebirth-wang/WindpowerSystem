package com.fastbee.icc.demo.visitor.visitorAppointment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.zxing.WriterException;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.fastbee.icc.model.visitor.visitorAppointment.VisitorAppointmentRequest;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorAppointmentResponse;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorTypePageRequest;
import com.fastbee.icc.util.FileUtil;
import com.fastbee.icc.util.QRCodeUtil;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-18 13:46
 * @Description: 访客预约测试
 */
public class VisitorAppointmentDemoTest {
    VisitorAppointmentDemo visitorAppointmentDemo = new VisitorAppointmentDemo();

    public VisitorAppointmentDemoTest() {
        visitorAppointmentDemo = new VisitorAppointmentDemo();
    }


    /**
     * 测试查询访客类型列表
     */
    @Test
    public void testGetVisitorTypePage(){
        VisitorTypePageRequest visitorTypePageRequest = new VisitorTypePageRequest();
        visitorTypePageRequest.setPageNum(1);
        visitorTypePageRequest.setPageSize(10);
        visitorAppointmentDemo.getVisitorTypePage(visitorTypePageRequest);
    }

    /**
     * 测试新增访客预约
     */
    @Test
    public void testAddVisitorAppointment(){
        try {
            VisitorAppointmentRequest visitorAppointmentRequest = new VisitorAppointmentRequest();
            visitorAppointmentRequest.setIsv_id(736612l);
            visitorAppointmentRequest.setV_name("访客1");
            visitorAppointmentRequest.setV_phone("17787879933");
            visitorAppointmentRequest.setV_dw("浙江大华技术股份有限公司");
            visitorAppointmentRequest.setV_address("浙江省杭州市滨江区1399号");
            visitorAppointmentRequest.setV_personSum(2);
            visitorAppointmentRequest.setV_timeStr("2024-04-18 15:00:00");
            visitorAppointmentRequest.setV_lvTimeStr("2024-04-18 17:00:00");
            visitorAppointmentRequest.setFaceFile(FileUtil.convertImageToBase64WithHeader("D://image.jpg"));
            visitorAppointmentRequest.setSecondVisitorType(963168253302013952l);
            visitorAppointmentRequest.setUserId(1);
            visitorAppointmentRequest.setAreaIdList(Arrays.asList(1168864676923047936l,1220037896581414912l));
            List<VisitorAppointmentRequest.FollowVisitor> followVisitors = new ArrayList<>();
            VisitorAppointmentRequest.FollowVisitor followVisitor = new VisitorAppointmentRequest.FollowVisitor();
            followVisitor.setV_name("随访人员1");
            followVisitor.setV_phone("17879836266");
            followVisitor.setFaceFile(FileUtil.convertImageToBase64WithHeader("D://image.jpg"));
            followVisitors.add(followVisitor);
            visitorAppointmentRequest.setFollowVisitors(followVisitors);
            VisitorAppointmentResponse response = visitorAppointmentDemo.addVisitorAppointment(visitorAppointmentRequest);
            //生成访客预约二维码图片
            try {
                if(response.getCode().equals("0")&&response.getData()!=null&&!StringUtils.isEmpty(response.getData().getV_barCode())) {
                    QRCodeUtil.generateQRCode(response.getData().getV_barCode(), 300, 300, "jpg", "D://test1.jpg");
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
