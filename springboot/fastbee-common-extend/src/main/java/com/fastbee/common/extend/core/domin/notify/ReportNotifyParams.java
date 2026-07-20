package com.fastbee.common.extend.core.domin.notify;

import java.util.Set;

import lombok.Data;

/**
 * @author fastb
 * @version 1.0
 * @description: TODO
 * @date 2023-12-26 11:03
 */
@Data
public class ReportNotifyParams {

    /**
     * 报表名称
     */
    private String name;
    /**
     * 状态
     */
    private String statusDesc;
    /**
     * 发送邮箱
     */
    private Set<String> sendEmail;
    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 报表文件路径
     */
    private String uploadPath;
}
