package com.fastbee.iot.model.gateWay;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author gsb
 * @date 2024/5/29 17:31
 */
@Data
public class SubDeviceListVO {

     private Long id;

     /** 网关设备id */
     @ApiModelProperty("网关设备编号")
     private String parentClientId;

     /** 子设备id */
     @ApiModelProperty("子设备编号")
     private String subClientId;

     /** 从机地址 */
     @ApiModelProperty("从机地址")
     private String address;
     /**
      * 子设备名称
      */
     @ApiModelProperty("子设备名称")
     private String subDeviceName;

     /** 创建时间 */
     @ApiModelProperty("创建时间")
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private Date createTime;

     private Long subProductId;

     private Long parentProductId;

     private Long subDeviceId;

     private Integer status;
}
