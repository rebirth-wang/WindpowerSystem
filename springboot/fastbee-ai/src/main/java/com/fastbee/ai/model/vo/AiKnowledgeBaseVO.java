package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 知识库展示对象。
 */
@Data
@ApiModel(value = "AiKnowledgeBaseVO", description = "AI 知识库展示对象")
public class AiKnowledgeBaseVO {

    @ApiModelProperty("知识库 ID")
    private Long knowledgeBaseId;

    @ApiModelProperty("知识库编码")
    private String kbCode;

    @ApiModelProperty("知识库名称")
    private String kbName;

    @ApiModelProperty("知识库类型")
    private String kbType;

    @ApiModelProperty("Embedding 厂商编码")
    private String embeddingProvider;

    @ApiModelProperty("向量库类型")
    private String vectorStoreType;

    @ApiModelProperty("模板编码")
    private String templateCode;

    @ApiModelProperty("模板版本")
    private String templateVersion;

    @ApiModelProperty("解析器类型")
    private String parserType;

    @ApiModelProperty("发布状态")
    private String publishStatus;

    @ApiModelProperty("当前激活版本 ID")
    private Long activeVersionId;

    @ApiModelProperty("当前激活版本号")
    private String activeVersionNo;

    @ApiModelProperty("当前激活向量库类型")
    private String activeVectorStoreType;

    @ApiModelProperty("扩展配置")
    private String extraConfig;

    @ApiModelProperty("租户 ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("状态")
    private String status;

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

    @ApiModelProperty("文档数量")
    private Integer documentCount;
}
