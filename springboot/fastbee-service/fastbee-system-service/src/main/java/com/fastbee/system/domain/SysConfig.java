package com.fastbee.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 参数配置对象 sys_config
 *
 * @author zhuangpeng.li
 * @date 2024-12-12
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysConfig", description = "参数配置 sys_config")
@Data
@TableName("sys_config" )
public class SysConfig extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 参数主键 */
    @TableId(value = "config_id", type = IdType.AUTO)
    @ApiModelProperty("参数主键")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long configId;

    /** 参数名称 */
    @ApiModelProperty("参数名称")
    private String configName;

    /** 参数键名 */
    @ApiModelProperty("参数键名")
    private String configKey;

    /** 参数键值 */
    @ApiModelProperty("参数键值")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @ApiModelProperty("系统内置")
    @AiSemanticField(
            semanticName = "系统内置",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_yes_no"
    )
    private String configType;

    /** 是否加密（0-否 1-是） */
    @AiSemanticField(
            semanticName = "是否加密",
            semanticType = AiSemanticType.ENUM
    )
    private Integer isEncryption;

}
