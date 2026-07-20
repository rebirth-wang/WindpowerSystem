package com.fastbee.ai.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * AI 厂商配置对象 ai_provider。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiProvider", description = "AI 厂商配置")
@TableName("ai_provider")
public class AiProvider extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "provider_id", type = IdType.AUTO)
    @ApiModelProperty("厂商 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long providerId;

    @ApiModelProperty("厂商编码")
    @AiSemanticField(
            semanticName = "厂商编码",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_code",
            synonyms = {"模型厂商", "服务厂商", "供应商"},
            queryHint = "check-value-mapping"
    )
    private String providerCode;

    @ApiModelProperty("厂商名称")
    private String providerName;

    @ApiModelProperty("区域档位")
    @AiSemanticField(
            semanticName = "区域档位",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_region_profile",
            synonyms = {"区域", "区域配置"},
            queryHint = "check-value-mapping"
    )
    private String regionProfile;

    @ApiModelProperty("接口基础地址")
    private String apiBaseUrl;

    @ApiModelProperty("鉴权方式")
    @AiSemanticField(
            semanticName = "鉴权方式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_auth_type",
            queryHint = "check-value-mapping"
    )
    private String authType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ApiModelProperty("加密后的密钥")
    private String apiKeyCipher;

    @ApiModelProperty("扩展配置")
    private String extraConfig;

    @ApiModelProperty("排序")
    private Integer sortNum;

    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_status",
            queryHint = "check-value-mapping"
    )
    private String status;

    @ApiModelProperty("租户 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
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

    @TableLogic
    @ApiModelProperty("删除标记")
    private String delFlag;
}
