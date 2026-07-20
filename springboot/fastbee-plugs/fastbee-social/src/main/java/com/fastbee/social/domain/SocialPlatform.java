package com.fastbee.social.domain;

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
 * 第三方登录平台控制对象 iot_social_platform
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SocialPlatform", description = "第三方登录平台控制 iot_social_platform")
@Data
@TableName("iot_social_platform" )
public class SocialPlatform extends BaseEntity{
    /** 第三方登录平台主键 */
    @TableId(value = "social_platform_id", type = IdType.AUTO)
    @ApiModelProperty("第三方登录平台主键")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long socialPlatformId;

    /** 第三方登录平台 */
    @ApiModelProperty("第三方登录平台")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_social_platform"
    )
    private String platform;

    /**  0:启用 ,1:禁用 */
    @ApiModelProperty(" 0:启用 ,1:禁用")
    @AiSemanticField(
            semanticName = "0",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_social_platform_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 第三方平台申请Id */
    @ApiModelProperty("第三方平台申请Id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String clientId;

    /** 第三方平台密钥 */
    @ApiModelProperty("第三方平台密钥")
    private String secretKey;

    /** 用户认证后跳转地址 */
    @ApiModelProperty("用户认证后跳转地址")
    private String redirectUri;

    /** 删除标记位(0代表存在，1代表删除) */
    @ApiModelProperty("删除标记位(0代表存在，1代表删除)")
    @TableLogic(value = "0", delval = "NULL")
    @AiSemanticField(
            semanticName = "删除标记位",
            semanticType = AiSemanticType.ENUM
    )
    private String delFlag;

    /** 绑定注册登录uri,http://localhost/login?bindId= */
    @ApiModelProperty("绑定注册登录uri,http://localhost/login?bindId=")
    private String bindUri;

    /** 跳转登录uri,http://localhost/login?loginId= */
    @ApiModelProperty("跳转登录uri,http://localhost/login?loginId=")
    private String redirectLoginUri;

    /** 错误提示uri,http://localhost/login?errorId= */
    @ApiModelProperty("错误提示uri,http://localhost/login?errorId=")
    private String errorMsgUri;

}
