package com.fastbee.icc.model.visitor.visitorAppointment;

import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-19 10:13
 * @Description: 查询访客类型列表请求参数
 */
@Data
public class VisitorTypePageRequest {
    /** 每页条数 */
    private Integer pageSize;
    /** 页码 */
    private Integer pageNum;
}
