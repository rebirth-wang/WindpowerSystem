package com.fastbee.ai.model.template;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 问数语义模板行对象。
 */
@Data
public class AiNl2SqlSemanticTemplateRow {

    @Excel(name = "表名", prompt = "系统自动带出，一般不需要修改")
    private String tableName;

    @Excel(name = "表说明", prompt = "系统自动带出，帮助理解表用途")
    private String tableComment;

    @Excel(name = "字段名", prompt = "系统自动带出，一般不需要修改")
    private String columnName;

    @Excel(name = "字段说明", prompt = "系统自动带出，可按业务语义微调")
    private String columnComment;

    @Excel(name = "语义名称", prompt = "默认已自动生成，可改成业务更容易理解的名称")
    private String semanticName;

    @Excel(
            name = "语义类型",
            combo = {"RELATION_KEY", "ENUM", "DIMENSION"},
            prompt = "默认已自动填写。关系键=主外键/关联键；枚举字段=状态/类型/启停类字段；普通维度字段=其他业务字段"
    )
    private String semanticType;

    @Excel(
            name = "来源类型",
            combo = {"MANUAL", "DICT", "ENUM", "AUTO_COMMENT"},
            prompt = "这列只说明“来源类别”，不等于来源编码。MANUAL=人工补充；DICT=系统字典；ENUM=枚举类；AUTO_COMMENT=字段注释自动识别"
    )
    private String sourceType;

    @Excel(name = "来源编码", prompt = "这列是“具体来源标识”。当来源类型=DICT 时，填字典类型；当来源类型=ENUM 时，填枚举类全限定名；MANUAL 或 AUTO_COMMENT 一般留空")
    private String sourceCode;

    @Excel(name = "关联提示", prompt = "可选。多个提示用分号分隔，例如：iot_device.product_id=iot_product.product_id")
    private String relationHints;

    @Excel(name = "值映射", prompt = "可选。推荐格式：在线=1;离线=0;启用=1;停用=0")
    private String valueMappings;

    @Excel(name = "同义词", prompt = "可选。多个同义词用分号分隔，例如：设备编号;序列号")
    private String aliases;

    @Excel(name = "查询提示", prompt = "可选。填写问数提示词，例如：默认统计当天;默认只看启用数据")
    private String queryHints;

    @Excel(name = "备注", prompt = "可选。补充边界、特殊口径或字段说明")
    private String remark;
}
