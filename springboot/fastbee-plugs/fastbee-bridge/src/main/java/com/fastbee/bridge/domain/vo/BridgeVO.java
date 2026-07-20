package com.fastbee.bridge.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 数据桥接对象 bridge
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "BridgeVO", description = "数据桥接 bridge")
@Data
public class BridgeVO{

    /** id唯一标识 */
    @Excel(name = "id唯一标识")
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 桥接配置信息 */
    @Excel(name = "桥接配置信息")
    @ApiModelProperty("桥接配置信息")
    private String configJson;

    /** 连接器名称 */
    @Excel(name = "连接器名称")
    @ApiModelProperty("连接器名称")
    private String name;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    @Excel(name = "是否生效")
    private String enable;

    /** 状态（0-未连接，1-连接中） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Integer status;

    /** 桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储) */
    @Excel(name = "桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)")
    @ApiModelProperty("桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)")
    private Integer type;

    /** 桥接方向(1=输入，2=输出) */
    @Excel(name = "桥接方向(1=输入，2=输出)")
    @ApiModelProperty("桥接方向(1=输入，2=输出)")
    private Integer direction;

    /** 转发路由（mqtt topic，http url） */
    @ApiModelProperty("转发路由")
    @Excel(name = "转发路由")
    private String route;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;


}
