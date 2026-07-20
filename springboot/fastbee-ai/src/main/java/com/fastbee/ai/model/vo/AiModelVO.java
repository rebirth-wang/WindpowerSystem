package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 模型展示对象。
 */
@Data
@ApiModel(value = "AiModelVO", description = "AI 模型展示对象")
public class AiModelVO {

    @ApiModelProperty("模型 ID")
    private Long modelId;

    @ApiModelProperty("厂商 ID")
    private Long providerId;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型类型")
    private String modelType;

    @ApiModelProperty("上下文长度")
    private Integer contextLength;

    @ApiModelProperty("默认推理参数")
    private String requestOptions;

    @ApiModelProperty("是否默认")
    private String isDefault;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("租户 ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建者")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("厂商编码")
    private String providerCode;

    @ApiModelProperty("厂商名称")
    private String providerName;
}
