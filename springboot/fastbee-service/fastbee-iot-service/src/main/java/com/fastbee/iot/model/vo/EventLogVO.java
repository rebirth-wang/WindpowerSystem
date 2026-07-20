package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;

import com.fastbee.common.annotation.Excel;

/**
 * 事件日志对象 iot_event_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "EventLogVO", description = "事件日志 iot_event_log")
@Data
public class EventLogVO{

    /** 设备事件日志ID */
    @Excel(name = "设备事件日志ID")
    @ApiModelProperty("设备事件日志ID")
    private Long logId;

    /** 标识符 */
    @Excel(name = "标识符")
    @ApiModelProperty("标识符")
    private String identify;

    /** 物模型名称 */
    @Excel(name = "物模型名称")
    @ApiModelProperty("物模型名称")
    private String modelName;

    /** 类型（3=事件上报，5=设备上线，6=设备离线） */
    @ApiModelProperty("类型")
    @Excel(name = "类型")
    private Integer logType;

    /** 日志值 */
    @Excel(name = "日志值")
    @ApiModelProperty("日志值")
    private String logValue;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 是否监测数据（1=是，0=否） */
    @ApiModelProperty("是否监测数据")
    @Excel(name = "是否监测数据")
    private Integer isMonitor;

    /** 模式(1=影子模式，2=在线模式，3=其他) */
    @Excel(name = "模式(1=影子模式，2=在线模式，3=其他)")
    @ApiModelProperty("模式(1=影子模式，2=在线模式，3=其他)")
    private Integer mode;

    /** 用户ID */
    @Excel(name = "用户ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户昵称 */
    @Excel(name = "用户昵称")
    @ApiModelProperty("用户昵称")
    private String userName;

    /** 租户ID */
    @Excel(name = "租户ID")
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

    private List<String> identityList;

}
