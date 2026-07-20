package com.fastbee.ai.service.impl;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.service.IAiNl2SqlDataScopeService;
import com.fastbee.common.annotation.DataScope;
import com.fastbee.common.extend.aspectj.DataScopeAspect;

/**
 * AI 智能问数数据范围注入服务实现。
 */
@Service
public class AiNl2SqlDataScopeServiceImpl implements IAiNl2SqlDataScopeService {

    @Override
    @DataScope
    public void applyTenantScope(AiNl2SqlQueryRequest request) {
        // 数据范围由 AOP 注入到 request.params 中，这里无需额外逻辑。
    }

    @Override
    @DataScope(fieldAlias = DataScopeAspect.DATA_SCOPE_FILED_ALIAS_USER_ID)
    public void applyUserScope(AiNl2SqlQueryRequest request) {
        // 数据范围由 AOP 注入到 request.params 中，这里无需额外逻辑。
    }
}
