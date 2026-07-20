package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 知识库版本展示对象。
 */
@Data
@ApiModel(value = "AiKnowledgeVersionVO", description = "AI 知识库版本展示对象")
public class AiKnowledgeVersionVO {

    @ApiModelProperty("版本 ID")
    private Long versionId;

    @ApiModelProperty("知识库 ID")
    private Long knowledgeBaseId;

    @ApiModelProperty("版本号")
    private String versionNo;

    @ApiModelProperty("模板编码")
    private String templateCode;

    @ApiModelProperty("模板版本")
    private String templateVersion;

    @ApiModelProperty("来源文档 ID 集合")
    private String sourceDocumentIds;

    @ApiModelProperty("结构化快照路径")
    private String snapshotPath;

    @ApiModelProperty("向量库类型")
    private String vectorStoreType;

    @ApiModelProperty("解析器类型")
    private String parserType;

    @ApiModelProperty("发布状态")
    private String publishStatus;

    @ApiModelProperty("是否激活(0否 1是)")
    private String isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发布时间")
    private Date publishTime;

    @ApiModelProperty("发布人")
    private String publishedBy;

    @ApiModelProperty("回滚来源版本")
    private String rollbackFromVersion;

    @ApiModelProperty("参与文件数")
    private Integer sourceFileCount;

    @ApiModelProperty("合并条目数")
    private Integer mergedItemCount;

    @ApiModelProperty("覆盖次数")
    private Integer overrideCount;

    @ApiModelProperty("冲突次数")
    private Integer conflictCount;

    @ApiModelProperty("构建摘要")
    private String buildSummary;

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
}
