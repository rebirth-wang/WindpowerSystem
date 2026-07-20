package com.fastbee.iot.model.vo;

import lombok.Data;

/**
 * @author zzy
 * @description: 新增网关时导入子产品
 * @date 2025-07-25 14:57
 */
@Data
public class SubProductImportVO {

    private String subProductName;

    private String address;

    private String thingsModelFilePath;
    private String modbusJobFilePath;
    private String ioRegisterFilePath;
    private String dataRegisterFilePath;
}
