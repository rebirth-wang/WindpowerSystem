package com.fastbee.bridge.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 数据桥接对象 bridge
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Bridge", description = "数据桥接 bridge")
@Data
@TableName("iot_bridge" )
public class Bridge extends BaseEntity{
    /** id唯一标识 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 桥接配置信息
     *  postgres数据库不能将string映射为json，typeHandler手动处理
     * */
    @ApiModelProperty("桥接配置信息")
    @AiSemanticField(relationHint = "version字段从json中取，DICT为mqtt_version")
    private String configJson;

    /** 连接器名称 */
    @ApiModelProperty("连接器名称")
    private String name;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    @AiSemanticField(
            semanticName = "是否生效",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String enable;

    /** 状态（0-未连接，1-连接中） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "bridge_status",
            valueMappings = {"-未连接，1-连接中=0"},
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储) */
    @ApiModelProperty("桥接类型(3=Http推送，4=Mqtt桥接，5=数据库存储)")
    @AiSemanticField(
            semanticName = "桥接类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "bridge_type"
    )
    private Long type;

    /** 桥接方向(1=输入，2=输出) */
    @ApiModelProperty("桥接方向(1=输入，2=输出)")
    @AiSemanticField(
            semanticName = "桥接方向",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "bridging_direction"
    )
    private Long direction;

    /** 转发路由（mqtt topic，http url） */
    @ApiModelProperty("转发路由")
    @AiSemanticField(
            semanticName = "转发路由",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String route;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

}
