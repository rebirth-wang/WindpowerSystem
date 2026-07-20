package com.fastbee.common.extend.core.domin.mq.message;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

import com.fastbee.common.extend.core.domin.mq.SubDeviceBo;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

/**
 * 上报数据模型bo
 * @author bill
 */
@Data
@Accessors(chain = true)
public class ReportDataBo {

   /**产品id*/
   private Long productId;
   /**设备编号*/
   private String serialNumber;
   /**上报消息*/
   private String message;
   /**上报的数据*/
   private List<ThingsModelSimpleItem> dataList;
   /**设备影子*/
   private boolean isShadow;
   /**
    * 物模型类型
    * 1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线
    */
   private int type;
   /**是否执行规则引擎*/
   private boolean isRuleEngine;
//   /**从机编号*/
//   private Integer slaveId;
   /**
    * 上报原数据包
    */
   private Object sources;

//   private GwDeviceBo gwDeviceBo;

   /**
    * 子设备集合
    */
   private List<SubDeviceBo> subDeviceBoList;

   private String topic;
   private String payload;

//   private Long userId;
//   private String userName;
//   private String deviceName;

}
