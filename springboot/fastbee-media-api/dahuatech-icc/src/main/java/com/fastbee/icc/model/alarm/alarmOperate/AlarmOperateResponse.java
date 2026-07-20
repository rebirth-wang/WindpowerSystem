package com.fastbee.icc.model.alarm.alarmOperate;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-06 14:47
 * @Description: 报警主机操作控制返回参数
 */
@Data
public class AlarmOperateResponse extends IccResponse {
   private Object data;
}
