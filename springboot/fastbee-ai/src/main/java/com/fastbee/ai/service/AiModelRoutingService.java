package com.fastbee.ai.service;

import com.fastbee.ai.model.enums.AiRegionProfile;
import com.fastbee.ai.model.vo.AiModelRouteVO;

/**
 * AI 模型路由服务。
 */
public interface AiModelRoutingService {

    /**
     * 解析当前默认路由。
     *
     * @return 路由结果
     */
    AiModelRouteVO resolveDefaultRoute();

    /**
     * 按模型编码解析路由。
     *
     * @param modelCode 模型编码
     * @return 路由结果
     */
    AiModelRouteVO resolveByModelCode(String modelCode);

    /**
     * 按区域解析路由。
     *
     * @param region 区域档位
     * @return 路由结果
     */
    AiModelRouteVO resolveByRegion(AiRegionProfile region);
}
