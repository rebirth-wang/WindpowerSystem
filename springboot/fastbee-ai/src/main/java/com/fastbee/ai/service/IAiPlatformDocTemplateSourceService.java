package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.model.template.AiPlatformDocTemplateRow;

/**
 * 平台知识模板导出源服务。
 */
public interface IAiPlatformDocTemplateSourceService {

    /**
     * 加载平台知识企业版模板行。
     *
     * @param sourceStrategy 来源策略，支持 AUTO / LOCAL / WEB
     * @return 企业版模板行列表
     */
    List<AiPlatformDocTemplateRow> loadTemplateRows(String sourceStrategy);
}
