package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 知识文档展示对象。
 */
@Data
@ApiModel(value = "AiKnowledgeDocumentVO", description = "AI 知识文档展示对象")
public class AiKnowledgeDocumentVO {

    @ApiModelProperty("文档 ID")
    private Long documentId;

    @ApiModelProperty("知识库 ID")
    private Long knowledgeBaseId;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("模板编码")
    private String templateCode;

    @ApiModelProperty("模板版本")
    private String templateVersion;

    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("文件校验码")
    private String checksum;

    @ApiModelProperty("解析状态")
    private String parseStatus;

    @ApiModelProperty("分片数量")
    private Integer chunkCount;

    @ApiModelProperty("解析快照路径")
    private String parsedSnapshotPath;

    @ApiModelProperty("解析摘要")
    private String parsedSummary;

    @ApiModelProperty("排序号")
    private Integer sortNum;

    @ApiModelProperty("来源类型")
    private String sourceOrigin;

    @ApiModelProperty("平台版本")
    private String appVersion;

    @ApiModelProperty("发布状态")
    private String publishStatus;

    @ApiModelProperty("发布版本号")
    private String publishedVersion;

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
}
