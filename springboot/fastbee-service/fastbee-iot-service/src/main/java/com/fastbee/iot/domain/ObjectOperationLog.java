package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 对象操作记录对象 object_operation_log
 *
 * @author fastbee
 * @date 2025-08-21
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ObjectOperationLog", description = "对象操作记录 object_operation_log")
@Data
@TableName("object_operation_log" )
public class ObjectOperationLog extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 对象主键id */
    @ApiModelProperty("对象主键id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long objectId;

    /** 类型 */
    @ApiModelProperty("类型")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer type;

    /** 变更内容 */
    @ApiModelProperty("变更内容")
    private String content;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Long delFlag;


}
