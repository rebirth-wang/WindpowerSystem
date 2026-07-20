package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 厂商展示对象。
 */
@Data
@ApiModel(value = "AiProviderVO", description = "AI 厂商展示对象")
public class AiProviderVO {

    @ApiModelProperty("厂商 ID")
    private Long providerId;

    @ApiModelProperty("厂商编码")
    private String providerCode;

    @ApiModelProperty("厂商名称")
    private String providerName;

    @ApiModelProperty("区域档位")
    private String regionProfile;

    @ApiModelProperty("接口基础地址")
    private String apiBaseUrl;

    @ApiModelProperty("鉴权方式")
    private String authType;

    @ApiModelProperty("扩展配置")
    private String extraConfig;

    @ApiModelProperty("排序")
    private Integer sortNum;

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

    @ApiModelProperty("密钥脱敏展示")
    private String apiKeyPreview;

    @ApiModelProperty("是否已配置密钥")
    private Boolean hasApiKey;
}
