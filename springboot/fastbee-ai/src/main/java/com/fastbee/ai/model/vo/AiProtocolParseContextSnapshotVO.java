package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 协议解析会话私有上下文快照。
 */
@Data
public class AiProtocolParseContextSnapshotVO {

    /**
     * 最近一次协议解析问题。
     */
    private String question;

    /**
     * 最近一次命中的知识库编码。
     */
    private String kbCode;

    /**
     * 最近一次命中的知识库名称。
     */
    private String kbName;

    /**
     * 最近一次命中的版本号。
     */
    private String versionNo;

    /**
     * 最近一次上下文来源。
     */
    private String runtimeSource;

    /**
     * 最近一次命中的模块名称。
     */
    private String moduleName;

    /**
     * 最近一次命中的字段名称。
     */
    private String fieldName;

    /**
     * 最近一次命中的字段编码。
     */
    private String fieldCode;

    /**
     * 最近一次命中的数据类型。
     */
    private String dataType;

    /**
     * 最近一次命中的示例值。
     */
    private String sampleValue;

    /**
     * 最近一次命中的值映射。
     */
    private String valueMappings;

    /**
     * 最近一次命中的备注说明。
     */
    private String remark;

    /**
     * 最近一次命中的别名。
     */
    private List<String> aliases = new ArrayList<>();
}
