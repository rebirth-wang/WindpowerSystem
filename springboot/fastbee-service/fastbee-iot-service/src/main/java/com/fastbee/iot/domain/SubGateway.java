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
 * 网关与子设备关联对象 iot_sub_gateway
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SubGateway", description = "网关与子设备关联 iot_sub_gateway")
@Data
@TableName("iot_sub_gateway" )
public class SubGateway extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 业务id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("业务id")
    private Long id;

    /** 网关设备编号 */
    @ApiModelProperty("网关设备编号")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String parentClientId;

    /** 子设备编号 */
    @ApiModelProperty("子设备编号")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String subClientId;

    /** 从机地址 */
    @ApiModelProperty("从机地址")
    private String address;

    /** 网关产品id */
    @ApiModelProperty("网关产品id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long parentProductId;

    /** 子设备产品id */
    @ApiModelProperty("子设备产品id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long subProductId;

}
