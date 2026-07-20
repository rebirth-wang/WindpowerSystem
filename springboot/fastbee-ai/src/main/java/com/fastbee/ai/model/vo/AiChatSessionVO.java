package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 会话展示对象。
 */
@Data
@ApiModel(value = "AiChatSessionVO", description = "AI 会话展示对象")
public class AiChatSessionVO {

    @ApiModelProperty("会话 ID")
    private Long sessionId;

    @ApiModelProperty("会话编码")
    private String sessionCode;

    @ApiModelProperty("会话标题")
    private String sessionTitle;

    @ApiModelProperty("用户 ID")
    private Long userId;

    @ApiModelProperty("租户 ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("厂商编码")
    private String providerCode;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("兼容会话模式快照")
    private String chatMode;

    @ApiModelProperty("会话策略")
    private String modePolicy;

    @ApiModelProperty("会话锁定模式")
    private String pinnedMode;

    @ApiModelProperty("最近实际能力")
    private String lastEffectiveMode;

    @ApiModelProperty("是否归档")
    private String isArchived;

    @ApiModelProperty("状态")
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

    @ApiModelProperty("消息数量")
    private Integer messageCount;
}
