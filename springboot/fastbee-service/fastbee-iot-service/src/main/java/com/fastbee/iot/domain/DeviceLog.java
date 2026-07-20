package com.fastbee.iot.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备日志对象 iot_device_log
 *
 * @author kerwincui
 * @date 2022-01-13
 */

@ApiModel(value = "DeviceLog", description = "设备日志对象 iot_device_log")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("iot_device_log" )
public class DeviceLog extends BaseEntity {
    @Excel(name = "时间戳")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ts;
    /**
     * 设备日志ID
     */
    @ApiModelProperty("设备日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long logId;

    /**
     * 类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线，7=运算数据上报）
     */
    @ApiModelProperty(value = "类型", notes = "1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线，7=运算数据上报, 8=设备禁用")
    @Excel(name = "类型", readConverterExp = "1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线，7=运算数据上报, 8=设备禁用")
    @AiSemanticField(
            semanticName = "类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Byte logType;

    /**
     * 日志值
     */
    @ApiModelProperty("日志值")
    @Excel(name = "日志值")
    private String logValue;

    /**
     * 物模型名称
     */
    @ApiModelProperty("物模型名称")
    @Excel(name = "物模型名称")
    private String modelName;

    /**
     * 设备ID
     */
    @ApiModelProperty("设备ID")
    @Excel(name = "设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_log.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty("设备名称")
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 设备编号
     */
    @ApiModelProperty("设备编号")
    @Excel(name = "设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_log.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /**
     * 标识符
     */
    @ApiModelProperty("标识符")
    @Excel(name = "标识符")
    private String identify;

    /**
     * 是否监测数据（1=是，0=否）
     */
    @ApiModelProperty("是否监测数据（1=是，0=否）")
    @Excel(name = "是否监测数据", readConverterExp = "1=是，0=否")
    @AiSemanticField(
            semanticName = "是否监测数据",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Byte isMonitor;

    /**
     * 模式
     */
    @ApiModelProperty(value = "模式", notes = "1=影子模式，2=在线模式,3=其他")
    @Excel(name = "模式", readConverterExp = "1=影子模式，2=在线模式,3=其他")
    private Byte mode;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @Excel(name = "用户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_log.user_id=sys_user.user_id"
    )
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty("用户昵称")
    @Excel(name = "用户昵称")
    private String userName;

    /**
     * 租户ID
     */
    @ApiModelProperty("租户ID")
    @Excel(name = "租户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty("租户名称")
    @Excel(name = "租户名称")
    private String tenantName;

    /**
     * 查询用的开始时间
     */
    @ApiModelProperty("查询用的开始时间")
    private String beginTime;

    /**
     * 查询用的结束时间
     */
    @ApiModelProperty("查询用的结束时间")
    private String endTime;

    /**
     * 查询的总数
     */
    @ApiModelProperty("查询的总数")
    private int total;

    /*消息ID，或消息流水号*/
    @ApiModelProperty("消息ID，或消息流水号")
    private String serNo;

    private String specs;

    private DataType dataType;

    private Integer slaveId;
    /**
     * 计算公式
     */
    private String formula;

    private Integer isParams;

    /*是否历史存储*/
    private Integer isHistory;

    private Integer operation;


    private List<String> identityList;


    @ApiModel
    public static class DataType {
        private String type;
        private String falseText;
        private String trueText;
        private Integer maxLength;
        private String arrayType;
        private String unit;
        private BigDecimal min;
        private BigDecimal max;
        private BigDecimal step;
        private List<EnumItem> enumList;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getFalseText() {
            return falseText;
        }

        public void setFalseText(String falseText) {
            this.falseText = falseText;
        }

        public String getTrueText() {
            return trueText;
        }

        public void setTrueText(String trueText) {
            this.trueText = trueText;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
        }

        public String getArrayType() {
            return arrayType;
        }

        public void setArrayType(String arrayType) {
            this.arrayType = arrayType;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public BigDecimal getMin() {
            return min;
        }

        public void setMin(BigDecimal min) {
            this.min = min;
        }

        public BigDecimal getMax() {
            return max;
        }

        public void setMax(BigDecimal max) {
            this.max = max;
        }

        public BigDecimal getStep() {
            return step;
        }

        public void setStep(BigDecimal step) {
            this.step = step;
        }

        public List<EnumItem> getEnumList() {
            return enumList;
        }

        public void setEnumList(List<EnumItem> enumList) {
            this.enumList = enumList;
        }
    }

    @ApiModel
    public static class EnumItem {
        private String text;
        private String value;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
