package com.fastbee.ai.model.template;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 平台知识企业版模板行对象。
 */
@Data
public class AiPlatformDocTemplateRow {

    @Excel(name = "一级栏目", prompt = "系统自动生成，表示文档所属的一级栏目，一般不需要修改")
    private String sectionLevel1;

    @Excel(name = "栏目路径", prompt = "系统自动生成，表示一级栏目到页面标题的路径，如：操作手册/产品管理")
    private String sectionPath;

    @Excel(name = "页面标题", prompt = "系统自动生成，默认取自文档标题")
    private String pageTitle;

    @Excel(name = "标题路径", prompt = "系统自动生成，表示当前知识单元在页面中的标题层级路径")
    private String headingPath;

    @Excel(
            name = "知识类型",
            combo = {"OVERVIEW", "STEP", "CONFIG", "NOTICE", "FAQ", "PERMISSION", "GUIDE"},
            prompt = "系统尽量自动识别。OVERVIEW=概述；STEP=操作步骤；CONFIG=配置说明；NOTICE=注意事项；FAQ=常见问题；PERMISSION=权限口径；GUIDE=其他引导"
    )
    private String knowledgeType;

    @Excel(name = "菜单路径", prompt = "系统尽量自动生成，可按真实菜单入口微调，如：设备管理/产品管理")
    private String menuPath;

    @Excel(name = "适用角色", prompt = "系统尽量自动生成，多个角色用分号分隔，如：管理员;租户管理员;普通用户")
    private String targetRole;

    @Excel(name = "前置条件", prompt = "系统尽量自动生成，多个条件建议用分号分隔")
    private String preconditions;

    @Excel(name = "操作步骤", prompt = "系统尽量自动生成，建议保留操作顺序，多个步骤用分号分隔")
    private String actionSteps;

    @Excel(name = "结果说明", prompt = "系统尽量自动生成，描述完成操作后会看到什么结果")
    private String resultDesc;

    @Excel(name = "注意事项", prompt = "系统尽量自动生成，优先来自提示块、限制条件和失败说明")
    private String cautions;

    @Excel(name = "正文补充", prompt = "系统自动生成，保留未被结构化拆分的正文说明")
    private String content;

    @Excel(name = "标签", prompt = "系统尽量自动生成，多个标签用分号分隔")
    private String tags;

    @Excel(name = "同义问法", prompt = "系统尽量自动生成，多个问法用分号分隔")
    private String aliases;

    @Excel(name = "关联文档", prompt = "系统自动生成，推荐格式：文档标题=链接，多个用分号分隔")
    private String relatedDocs;

    @Excel(name = "来源文件", prompt = "系统自动生成，本地源码模式下记录相对路径")
    private String sourceFile;

    @Excel(name = "来源链接", prompt = "系统自动生成，对应官网公开页面地址")
    private String sourceUrl;

    @Excel(
            name = "来源类型",
            combo = {"LOCAL_DOC_SOURCE", "WEB_DOC_SOURCE", "MANUAL"},
            prompt = "系统自动生成。LOCAL_DOC_SOURCE=本地源码；WEB_DOC_SOURCE=官网抓取；MANUAL=人工补录"
    )
    private String sourceType;

    @Excel(name = "语言", combo = {"zh-CN", "en-US"}, prompt = "系统自动生成，中文文档为 zh-CN，英文文档为 en-US")
    private String language;

    @Excel(name = "文档版本", prompt = "系统自动生成，可用于记录文档版本或导出批次")
    private String docVersion;

    @Excel(name = "备注", prompt = "可选。补充边界、权限说明或无法结构化的信息")
    private String remark;
}
