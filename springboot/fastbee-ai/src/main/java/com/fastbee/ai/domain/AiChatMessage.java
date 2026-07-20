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
 * AI 会话消息对象 ai_chat_message。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiChatMessage", description = "AI 会话消息")
@TableName("ai_chat_message")
public class AiChatMessage extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "message_id", type = IdType.AUTO)
    @ApiModelProperty("消息 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long messageId;

    @ApiModelProperty("会话 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_chat_message.session_id=ai_chat_session.session_id"
    )
    private Long sessionId;

    @ApiModelProperty("消息序号")
    private Integer messageNo;

    @ApiModelProperty("角色类型")
    @AiSemanticField(
            semanticName = "角色类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"user=用户", "assistant=助手", "system=系统", "tool=工具"},
            queryHint = "check-value-mapping"
    )
    private String roleType;

    @ApiModelProperty("消息内容")
    private String messageContent;

    @ApiModelProperty("消息摘要")
    private String messageSummary;

    @ApiModelProperty("工具名称")
    private String toolName;

    @ApiModelProperty("工具结果")
    private String toolResult;

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

    @ApiModelProperty("能力类型")
    @AiSemanticField(
            semanticName = "能力类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"AUTO=自动识别", "PLATFORM_ASSISTANT=平台助手", "GENERAL=通用对话", "NL2SQL=智能问数", "DEVICE_CONTROL=设备控制", "PROTOCOL_PARSE=协议解析", "THING_MODEL_GENERATE=物模型生成", "REQUIREMENT_EVALUATION=需求评估", "GENERAL_CHAT=通用对话"},
            queryHint = "check-value-mapping"
    )
    private String abilityType;

    @ApiModelProperty("路由模式")
    @AiSemanticField(
            semanticName = "路由模式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"DEFAULT=默认路由", "MANUAL=手动指定"},
            queryHint = "check-value-mapping"
    )
    private String routeMode;

    @ApiModelProperty("Token 用量")
    private Integer tokenUsage;

    @ApiModelProperty("耗时毫秒")
    private Long durationMs;

    @ApiModelProperty("是否确认风险")
    @AiSemanticField(
            semanticName = "是否确认风险",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=否", "1=是"},
            queryHint = "check-value-mapping"
    )
    private String riskConfirmed;

    @ApiModelProperty("消息状态")
    @AiSemanticField(
            semanticName = "消息状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"RUNNING=运行中", "SUCCESS=成功", "FAIL=失败"},
            queryHint = "check-value-mapping"
    )
    private String messageStatus;

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
