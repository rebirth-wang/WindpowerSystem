package com.fastbee.sip.domain;

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
 * sip系统配置对象 sip_config
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SipConfig", description = "sip系统配置 sip_config")
@Data
@TableName("sip_config" )
public class SipConfig extends BaseEntity {
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sip_config.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 使能开关 */
    @ApiModelProperty("使能开关")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer enabled;

    /** 系统默认配置 */
    @ApiModelProperty("系统默认配置")
    private Integer isdefault;

    /** 拓展sdp */
    @ApiModelProperty("拓展sdp")
    private Integer seniorSdp;

    /** 服务器域 */
    @ApiModelProperty("服务器域")
    private String domainAlias;

    /** 服务器sipid */
    @ApiModelProperty("服务器sipid")
    private String serverSipid;

    /** sip认证密码 */
    @ApiModelProperty("sip认证密码")
    private String password;

    /** sip接入IP */
    @ApiModelProperty("sip接入IP")
    private String ip;

    /** sip接入端口号 */
    @ApiModelProperty("sip接入端口号")
    private Integer port;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

}
