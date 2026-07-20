package com.fastbee.iot.model.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO;

/**
 * 设备对象 iot_device
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceVO", description = "设备 iot_device")
@Data
public class DeviceVO extends PageEntity {

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 子设备网关编号 */
    @Excel(name = "子设备网关编号")
    @ApiModelProperty("子设备网关编号")
    private String gwDevCode;

    /** 固件版本 */
    @Excel(name = "固件版本")
    @ApiModelProperty("固件版本")
    private BigDecimal firmwareVersion;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    @Excel(name = "设备状态")
    private Integer status;

    /** 信号强度（
        信号极好4格[-55— 0]，
        信号好3格[-70— -55]，
        信号一般2格[-85— -70]，
        信号差1格[-100— -85]） */
    @ApiModelProperty("信号强度")
    @Excel(name = "信号强度")
    private Integer rssi;

    /** 是否启用设备影子(0=禁用，1=启用) */
    @Excel(name = "是否启用设备影子(0=禁用，1=启用)")
    @ApiModelProperty("是否启用设备影子(0=禁用，1=启用)")
    private Integer isShadow;

    /** 定位方式(1=ip自动定位，2=设备定位，3=自定义) */
    @Excel(name = "定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    @ApiModelProperty("定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    private Integer locationWay;

    /** 物模型值 */
    @Excel(name = "物模型值")
    @ApiModelProperty("物模型值")
    private String thingsModelValue;

    /** 设备所在地址 */
    @Excel(name = "设备所在地址")
    @ApiModelProperty("设备所在地址")
    private String networkAddress;

    /** 设备入网IP */
    @Excel(name = "设备入网IP")
    @ApiModelProperty("设备入网IP")
    private String networkIp;

    /** 设备经度 */
    @Excel(name = "设备经度")
    @ApiModelProperty("设备经度")
    private BigDecimal longitude;

    /** 设备纬度 */
    @Excel(name = "设备纬度")
    @ApiModelProperty("设备纬度")
    private BigDecimal latitude;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("激活时间")
    @Excel(name = "激活时间")
    private Date activeTime;

    /** 设备摘要，格式[{"name":"device"},{"chip":"esp8266"}] */
    @Excel(name = "设备摘要，格式[{\"name\":\"device\"},{\"chip\":\"esp8266\"}]")
    @ApiModelProperty("设备摘要，格式[{\"name\":\"device\"},{\"chip\":\"esp8266\"}]")
    private String summary;

    /** 图片地址 */
    @Excel(name = "图片地址")
    @ApiModelProperty("图片地址")
    private String imgUrl;

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

    /** 子设备地址 */
    @Excel(name = "子设备地址")
    @ApiModelProperty("子设备地址")
    private String address;


    /** 设备类型（1-直连设备、2-网关设备、3-监控设备） */
    @ApiModelProperty("设备类型（1-直连设备、2-网关设备、3-监控设备 4-网关子设备）")
    private Integer deviceType;

    /** 分组ID，用于分组查询 **/
    @ApiModelProperty("分组ID，用于分组查询")
    private Long groupId;

    /** 是否设备所有者，用于查询 **/
    @ApiModelProperty("是否设备所有者，用于查询")
    private Integer isOwner;
    /**子设备数量*/
    @ApiModelProperty("子设备数量")
    private Integer subDeviceCount;
    /**设备传输协议*/
    @ApiModelProperty("设备传输协议")
    private String transport;

    @ApiModelProperty("机构id")
    private Long deptId;

    @ApiModelProperty("是否显示下级")
    private Boolean showChild;

    private List<Device> subDeviceList;

    @ApiModelProperty("用户id")
    private Long userId;

    /**
     *  协议
     */
    private String protocolCode;

    /**
     * 关联组态,来源产品
     */
    private String guid;

    private List<SipRelationVO> sipRelationVOList;

    /**
     * 是否可以配置轮询
     */
    private Boolean canConfigPoll;

    /**
     * 机构名称
     */
    private String deptName;

    /**
     * 设备编号和名称查询条件
     */
    private String query;

    /**
     * 固件类型
     */
    private Integer firmwareType;

    private String parentSerialNumber;
    /**
     * modbusTcp主站端口
     */
    private Integer devicePort;

    /**
     * modbusTcp主站ip
     */
    private String deviceIp;

    private Long gwDeviceId;

    private Long parentProductId;

    private Integer panelEnable;

    /**
     * 拖拽面板json数据
     */
    @ApiModelProperty("拖拽面板json数据")
    private String panelModelsJson;

    private Integer sortType;

    private List<ProductSubDeviceAddVO.SubProduct> subProductList;

    /** ICCID号 */
    @ApiModelProperty("ICCID号")
    private String iccid;

    /** 卡平台id */
    @ApiModelProperty("卡平台id")
    private Long cardPlatformId;

    private List<ProductExtParamVO> extParamList;

    private List<DeviceExtParamValue> deviceExtParamValueList;

}
