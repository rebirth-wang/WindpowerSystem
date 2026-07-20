package com.fastbee.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 系统授权对象 sys_client
 *
 * @author zhuangpeng.li
 * @date 2024-12-12
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysClient", description = "系统授权 sys_client")
@Data
@TableName("sys_client" )
public class SysClient extends BaseEntity {
    private static final long serialVersionUID=1L;

    /** id唯一标识 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 客户端key */
    @ApiModelProperty("客户端key")
    private String clientKey;

    /** 客户端秘钥 */
    @ApiModelProperty("客户端秘钥")
    private String clientSecret;

    /** 客户端token */
    @ApiModelProperty("客户端token")
    private String token;

    /** 授权类型 */
    @ApiModelProperty("授权类型")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private String grantType;

    /** 设备类型 */
    @ApiModelProperty("设备类型")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private String deviceType;

    /** token固定超时 */
    @ApiModelProperty("token固定超时")
    private Long timeout;

    /** 是否生效（0-不生效，1-生效） */
    @ApiModelProperty("是否生效")
    @AiSemanticField(
            semanticName = "是否生效",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String enable;

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

}
