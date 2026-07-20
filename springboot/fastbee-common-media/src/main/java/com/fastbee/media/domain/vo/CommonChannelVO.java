package com.fastbee.media.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;
import com.fastbee.media.domain.BindingChannel;

/**
 * 监控视频通道信息对象 common_channel
 *
 * @author fastbee
 * @date 2026-01-30
 */

@ApiModel(value = "CommonChannelVO", description = "监控视频通道信息 common_channel")
@Data
public class CommonChannelVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long tenantId;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private String deviceId;

    /** 通道ID */
    @Excel(name = "通道ID")
    @ApiModelProperty("通道ID")
    private String channelId;

    /** 通道名称 */
    @Excel(name = "通道名称")
    @ApiModelProperty("通道名称")
    private String channelName;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("注册时间")
    @Excel(name = "注册时间")
    private Date registerTime;

    /** 设备类型 */
    @Excel(name = "设备类型")
    @ApiModelProperty("设备类型")
    private String deviceType;

    /** 通道类型 */
    @Excel(name = "通道类型")
    @ApiModelProperty("通道类型")
    private String channelType;


    /** 流数据类型 */
    @Excel(name = "流数据类型")
    @ApiModelProperty("流数据类型")
    private Integer dataType;

    /** 城市编码 */
    @Excel(name = "城市编码")
    @ApiModelProperty("城市编码")
    private String cityCode;

    /** 行政区域 */
    @Excel(name = "行政区域")
    @ApiModelProperty("行政区域")
    private String civilCode;

    /** 厂商名称 */
    @Excel(name = "厂商名称")
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @Excel(name = "产品型号")
    @ApiModelProperty("产品型号")
    private String model;

    /** 通道归属 */
    @Excel(name = "通道归属")
    @ApiModelProperty("通道归属")
    private String owner;

    /** 通道口令 */
    @Excel(name = "通道口令")
    @ApiModelProperty("通道口令")
    private String password;

    /** 父级id */
    @Excel(name = "父级id")
    @ApiModelProperty("父级id")
    private String parentId;

    /** 设备入网IP */
    @Excel(name = "设备入网IP")
    @ApiModelProperty("设备入网IP")
    private String ipAddress;

    /** 设备接入端口号 */
    @Excel(name = "设备接入端口号")
    @ApiModelProperty("设备接入端口号")
    private Long port;

    /** PTZ类型 */
    @Excel(name = "PTZ类型")
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** PTZ类型描述字符串 */
    @Excel(name = "PTZ类型描述字符串")
    @ApiModelProperty("PTZ类型描述字符串")
    private String ptzTypeText;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    @Excel(name = "设备状态")
    private Long status;

    /** 设备经度 */
    @Excel(name = "设备经度")
    @ApiModelProperty("设备经度")
    private Long longitude;

    /** 设备纬度 */
    @Excel(name = "设备纬度")
    @ApiModelProperty("设备纬度")
    private Long latitude;

    /** 流媒体ID */
    @Excel(name = "流媒体ID")
    @ApiModelProperty("流媒体ID")
    private String streamId;

    /** 通道播放地址 */
    @Excel(name = "通道播放地址")
    @ApiModelProperty("通道播放地址")
    private String playUrl;

    /** 流代理地址 */
    @Excel(name = "流代理地址")
    @ApiModelProperty("流代理地址")
    private String proxyUrl;

    /** 是否含有音频（1-有, 0-没有） */
    @ApiModelProperty("是否含有音频")
    @Excel(name = "是否含有音频")
    private Long hasAudio;

    /** 是否支持对讲（1-支持, 0-不支持） */
    @ApiModelProperty("是否支持对讲")
    @Excel(name = "是否支持对讲")
    private Long hasBroadcast;

    /** 设备控制配置 */
    @Excel(name = "设备控制配置")
    @ApiModelProperty("设备控制配置")
    private String ctrlConfig;

    /** 拓展信息 */
    @Excel(name = "拓展信息")
    @ApiModelProperty("拓展信息")
    private String extendInfo;

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


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /**
     * 推流状态（0-未使用，1-推流中）
     */
    @ApiModelProperty("推流状态（0-未使用，1-推流中）")
    @Excel(name = "推流状态", readConverterExp = "0-未使用，1-推流中")
    private Integer streamPush = 0;
    /**
     * 直播录像状态（0-未使用，1-录像中）
     */
    @ApiModelProperty("直播录像状态（0-未使用，1-录像中）")
    @Excel(name = "直播录像状态", readConverterExp = "0-未使用，1-录像中")
    private Integer streamRecord = 0;

    /**
     * 录像转存状态（0-未使用，1-转存中）
     */
    @ApiModelProperty("录像转存状态（0-未使用，1-转存中）")
    @Excel(name = "录像转存状态", readConverterExp = "0-未使用，1-转存中")
    private Integer videoRecord = 0;

    @ApiModelProperty("关联信息")
    private BindingChannel bindingChannel;

    /**
     * 设备id
     */
    @ApiModelProperty("关联设备id")
    private Long reDeviceId;

    /**
     * 场景id
     */
    @ApiModelProperty("关联场景id")
    private Long reSceneModelId;
    @ApiModelProperty("关联设备名称")
    private String reDeviceName;
    @ApiModelProperty("关联场景名称")
    private String reSceneModelName;

    /** 自定义代码区域 END**/
}
