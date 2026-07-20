package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 会话执行阶段的全局上下文摘要。
 */
@Data
public class AiConversationGlobalContextVO {

    /**
     * 最近两轮用户主题。
     */
    private List<String> recentUserTopics = new ArrayList<>();

    /**
     * 当前关注设备名称。
     */
    private String focusDeviceName;

    /**
     * 当前关注设备编号。
     */
    private String focusSerialNumber;

    /**
     * 当前关注产品名称。
     */
    private String focusProductName;

    /**
     * 当前关注物模型名称。
     */
    private String focusThingModelName;

    /**
     * 当前关注标识符。
     */
    private String focusIdentifier;

    /**
     * 当前关注菜单路径。
     */
    private String focusMenuPath;

    /**
     * 当前关注页面标题。
     */
    private String focusPageTitle;

    /**
     * 当前关注协议模块。
     */
    private String focusProtocolModuleName;

    /**
     * 当前关注协议字段名称。
     */
    private String focusProtocolFieldName;

    /**
     * 当前关注协议字段编码。
     */
    private String focusProtocolFieldCode;

    /**
     * 当前关注协议字段数据类型。
     */
    private String focusProtocolDataType;
}
