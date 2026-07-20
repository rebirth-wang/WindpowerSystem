package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 监控设备关联对象 iot_sip_relation
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SipRelation", description = "监控设备关联 iot_sip_relation")
@Data
@TableName("iot_sip_relation" )
public class SipRelation extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 监控设备编号 */
    @ApiModelProperty("监控设备编号")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String channelId;

    /** 关联的设备id */
    @ApiModelProperty("关联的设备id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reDeviceId;

    /** 关联的场景id */
    @ApiModelProperty("关联的场景id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reSceneModelId;

}
