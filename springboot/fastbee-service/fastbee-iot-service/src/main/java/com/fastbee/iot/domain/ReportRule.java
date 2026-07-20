package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 报表规则对象 report_rule
 *
 * @author zzy
 * @date 2025-07-10
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportRule", description = "报表规则 report_rule")
@Data
@TableName("report_rule" )
public class ReportRule extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @ApiModelProperty("报表id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reportId;

    /** 场景或设备id */
    @ApiModelProperty("场景或设备id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long cusSourceId;

    /** 场景关联设备id */
    @ApiModelProperty("场景关联设备id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneModelDeviceId;


}
