package com.fastbee.ai.guard;

import com.fastbee.ai.model.vo.AiSqlGuardResultVO;

/**
 * AI SQL 守卫服务接口。
 */
public interface IAiSqlGuardService {

    /**
     * 校验并归一化只读查询 SQL。
     *
     * @param sqlText 原始 SQL
     * @param rowLimit 期望行数
     * @return 守卫结果
     */
    AiSqlGuardResultVO guardReadOnlySelect(String sqlText, Integer rowLimit);
}
