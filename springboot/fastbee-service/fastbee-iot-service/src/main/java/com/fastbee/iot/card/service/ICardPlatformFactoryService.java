package com.fastbee.iot.card.service;

import java.util.List;

import com.fastbee.common.extend.core.card.CardPlatformConfigContent;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.dto.TrafficInfoDTO;

/**
 * @description: 物联卡操作接口服务类
 * @author zzy
 * @date 2025-11-13 11:58
 */
public interface ICardPlatformFactoryService {

    /**
     * 获取卡详情
     *
     * @param iccid iccid号
     * @param configContent 平台配置内容
     * @return {@link Card }
     * @throws Exception 例外
     */
    Card syncCardInfo(String iccid, CardPlatformConfigContent configContent) throws Exception;

    /**
     * 批量获取卡详情
     *
     * @param iccids iccids
     * @param configContent 平台配置内容
     * @return {@link List }<{@link Card }>
     * @throws Exception 例外
     */
    List<Card> getBatchCardDetail(String iccids, CardPlatformConfigContent configContent) throws Exception;

    /**
     * 查询流量使用情况
     *
     * @param iccid iccid
     * @param configContent 平台配置内容
     * @return {@link TrafficInfoDTO }
     * @throws Exception 例外
     */
    TrafficInfoDTO syncTrafficInfo(String iccid, CardPlatformConfigContent configContent) throws Exception;

    /**
     * 同步状态
     *
     * @param iccid iccid
     * @param configContent 平台配置内容
     * @return {@link Integer }
     */
    Integer syncStatus(String iccid, CardPlatformConfigContent configContent) throws Exception;

    /**
     * 激活卡片
     *
     * @param iccid iccid
     * @return boolean
     * @throws Exception 例外
     */
    boolean activateCard(String iccid) throws Exception;

    /**
     * 停用卡片
     *
     * @param iccid iccid
     * @return boolean
     * @throws Exception 例外
     */
    boolean suspendCard(String iccid) throws Exception;

    /**
     * 恢复卡片
     *
     * @param iccid iccid
     * @return boolean
     * @throws Exception 例外
     */
    boolean resumeCard(String iccid) throws Exception;

}
