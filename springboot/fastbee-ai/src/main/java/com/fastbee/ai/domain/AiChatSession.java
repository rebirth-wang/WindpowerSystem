package com.fastbee.ai.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * AI 会话对象 ai_chat_session。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiChatSession", description = "AI 会话")
@TableName("ai_chat_session")
public class AiChatSession extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "session_id", type = IdType.AUTO)
    @ApiModelProperty("会话 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sessionId;

    @ApiModelProperty("会话编码")
    private String sessionCode;

    @ApiModelProperty("会话标题")
    private String sessionTitle;

    @ApiModelProperty("用户 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_chat_session.user_id=sys_user.user_id"
    )
    private Long userId;

    @ApiModelProperty("租户 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("厂商编码")
    @AiSemanticField(
            semanticName = "厂商编码",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_code",
            queryHint = "check-value-mapping"
    )
    private String providerCode;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("兼容会话模式快照")
    @AiSemanticField(
            semanticName = "会话模式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"AUTO=自动识别", "PLATFORM_ASSISTANT=平台助手", "GENERAL=通用对话", "NL2SQL=智能问数", "DEVICE_CONTROL=设备控制", "PROTOCOL_PARSE=协议解析", "THING_MODEL_GENERATE=物模型生成", "REQUIREMENT_EVALUATION=需求评估"},
            queryHint = "check-value-mapping"
    )
    private String chatMode;

    @ApiModelProperty("会话策略")
    @AiSemanticField(
            semanticName = "会话策略",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"AUTO=自动识别", "PINNED=锁定会话"},
            queryHint = "check-value-mapping"
    )
    private String modePolicy;

    @ApiModelProperty("会话锁定模式")
    @AiSemanticField(
            semanticName = "会话锁定模式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"PLATFORM_ASSISTANT=平台助手", "GENERAL=通用对话", "NL2SQL=智能问数", "DEVICE_CONTROL=设备控制", "PROTOCOL_PARSE=协议解析", "THING_MODEL_GENERATE=物模型生成", "REQUIREMENT_EVALUATION=需求评估"},
            queryHint = "check-value-mapping"
    )
    private String pinnedMode;

    @ApiModelProperty("最近实际能力")
    @AiSemanticField(
            semanticName = "最近实际能力",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"PLATFORM_ASSISTANT=平台助手", "GENERAL=通用对话", "NL2SQL=智能问数", "DEVICE_CONTROL=设备控制", "PROTOCOL_PARSE=协议解析", "THING_MODEL_GENERATE=物模型生成", "REQUIREMENT_EVALUATION=需求评估", "GENERAL_CHAT=通用对话"},
            queryHint = "check-value-mapping"
    )
    private String lastEffectiveMode;

    @ApiModelProperty("是否归档")
    @AiSemanticField(
            semanticName = "是否归档",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=否", "1=是"},
            queryHint = "check-value-mapping"
    )
    private String isArchived;

    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=正常", "1=停用"},
            queryHint = "check-value-mapping"
    )
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后消息时间")
    private Date lastMessageTime;

    @ApiModelProperty("创建者")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @TableLogic
    @ApiModelProperty("删除标记")
    private String delFlag;
}
