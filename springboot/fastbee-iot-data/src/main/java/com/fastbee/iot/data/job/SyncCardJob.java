package com.fastbee.iot.data.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.CardPlatformEnum;
import com.fastbee.iot.convert.CardConvert;
import com.fastbee.iot.domain.Card;
import com.fastbee.iot.model.vo.CardVO;
import com.fastbee.iot.service.ICardService;
import com.fastbee.notify.core.service.NotifySendService;

/**
 * 定时同步卡信息
 *
 * @author fastbee
 * @date 2025/12/23
 */
@Slf4j
@Component
public class SyncCardJob {
    @Resource
    private ICardService iCardService;
    @Resource
    private NotifySendService notifySendService;

    /**
     * 记录平台最后一次调用时间
     */
    private final Map<Long, Long> platformLastCallTime = new ConcurrentHashMap<>();

    /**
     * 针对 platform=zhiyu 的锁
     */
    private final Object platformZhiYuLock = new Object();
    /**
     * 30秒
     */
    private static final long PLATFORM_ZHI_YU_INTERVAL = 30_000;

    public void batchSyncTraffic() {
        log.info("开始批量同步流量信息...");

        // 查询所有激活的卡片
        CardVO cardVO = new CardVO();
//        cardVO.setCardStatus(CardStatusEnum.NORMAL.getStatus());
        List<CardVO> cardVOList = iCardService.selectCardAndPlatformList(cardVO);
        if (CollectionUtils.isEmpty(cardVOList)) {
            return;
        }
        log.info("找到 {} 张激活的卡片需要同步", cardVOList.size());

        // 按平台分组
        Map<Long, List<CardVO>> cardsByPlatform = cardVOList.stream()
                .collect(Collectors.groupingBy(CardVO::getCardPlatformId));

        CompletableFuture.runAsync(() -> {
            log.info("流量同步任务开始执行...");
            int successCount = 0;
            int failCount = 0;

            // 处理每个平台的数据
            for (Map.Entry<Long, List<CardVO>> entry : cardsByPlatform.entrySet()) {
                Long platformId = entry.getKey();
                List<CardVO> platformCards = entry.getValue();

                for (CardVO card : platformCards) {
                    try {
                        // 特殊处理
                        if (CardPlatformEnum.ZHIYU_IOT.getPlatform().equals(card.getPlatform())) {
                            processPlatform2Card(card);
                        } else {
                            // 其他平台正常处理
                            Card editCard = iCardService.syncTrafficInfo(card.getIccid(), platformId);
                            CardVO convertCardVO = CardConvert.INSTANCE.convertCardVO(editCard);
                            handleSyncResult(card, convertCardVO);
                        }
                        successCount++;

                    } catch (InterruptedException e) {
                        log.warn("同步流量任务被中断, ICCID: {}", card.getIccid());
                        Thread.currentThread().interrupt();
                        failCount++;
                        break;
                    } catch (Exception e) {
                        log.error("同步流量失败, ICCID: {}", card.getIccid(), e);
                        failCount++;
                    }
                }
            }

            log.info("批量同步流量信息完成, 成功: {} 张, 失败: {} 张, 总计: {} 张",
                    successCount, failCount, cardVOList.size());
        });

        log.info("已提交异步流量同步任务，任务在后台执行中...");
    }

    /**
     * 处理 platformId=2 的卡片，保证20秒间隔
     */
    private void processPlatform2Card(CardVO cardVO) throws InterruptedException {
        synchronized (platformZhiYuLock) {
            Long lastCallTime = platformLastCallTime.get(cardVO.getCardPlatformId());
            long currentTime = System.currentTimeMillis();

            if (lastCallTime != null) {
                long timeSinceLastCall = currentTime - lastCallTime;
                if (timeSinceLastCall < PLATFORM_ZHI_YU_INTERVAL) {
                    // 需要等待
                    long sleepTime = PLATFORM_ZHI_YU_INTERVAL - timeSinceLastCall;
                    log.info("等待 {}ms 以满足30秒间隔要求", sleepTime);
                    Thread.sleep(sleepTime);
                }
            }

            // 执行同步
            Card editCard = iCardService.syncTrafficInfo(cardVO.getIccid(), cardVO.getCardPlatformId());
            CardVO convertCardVO = CardConvert.INSTANCE.convertCardVO(editCard);
            handleSyncResult(cardVO, convertCardVO);

            // 更新最后调用时间
            platformLastCallTime.put(cardVO.getCardPlatformId(), System.currentTimeMillis());
        }
    }

    /**
     * 处理同步结果
     */
    private void handleSyncResult(CardVO card, CardVO editCard) throws InterruptedException {
        if (Objects.isNull(editCard)) {
            editCard = card;
        }

        if (null == editCard.getDataUsed() || null == editCard.getTotalData() || null == card.getDataAlertThreshold()) {
            return;
        }

        // 处理告警逻辑
        BigDecimal usageRate = editCard.getDataUsed()
                .divide(editCard.getTotalData(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (usageRate.compareTo(card.getDataAlertThreshold()) >= 0) {
            log.warn("流量告警 - ICCID: {}, 使用率: {}%, 阈值: {}%",
                    card.getIccid(), usageRate, card.getDataAlertThreshold());
            notifySendService.cardSend(editCard);
        }

        // 通用等待，避免请求过于频繁
        Thread.sleep(1000);
    }
}
