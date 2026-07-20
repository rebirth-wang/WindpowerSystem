package com.fastbee.oss.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 文件记录对象 oss_detail
 *
 * @author zhuangpeng.li
 * @date 2024-04-22
 */
@Builder
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OssDetail", description = "文件记录 oss_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("oss_detail" )
public class OssDetail extends PageEntity
{
    /** 文件id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("文件id")
    private Long id;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 文件名 */
    @ApiModelProperty("文件名")
    private String fileName;

    /** 原名 */
    @ApiModelProperty("原名")
    private String originalName;

    /** 文件后缀名 */
    @ApiModelProperty("文件后缀名")
    private String fileSuffix;

    /** URL地址 */
    @ApiModelProperty("URL地址")
    private String url;

    /** 服务商 */
    @ApiModelProperty("服务商")
    private String service;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

}
